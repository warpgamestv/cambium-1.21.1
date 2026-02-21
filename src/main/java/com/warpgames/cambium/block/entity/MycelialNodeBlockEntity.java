package com.warpgames.cambium.block.entity;

import com.warpgames.cambium.block.MycelialStrandBlock;
import com.warpgames.cambium.block.StrandConnection;
import com.warpgames.cambium.registry.ModBlockEntities;
import com.warpgames.cambium.registry.ModBlocks;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.IntStream;

public class MycelialNodeBlockEntity extends BlockEntity implements ExtendedScreenHandlerFactory<BlockPos> {

    private int scanTimer = 0;

    private final Set<BlockPos> connectedInventories = new HashSet<>();
    private final List<ItemStack> networkItems = new ArrayList<>();

    public MycelialNodeBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.MYCELIAL_NODE_BE, pos, state);
    }

    @Override
    public BlockPos getScreenOpeningData(ServerPlayer player) {
        return this.worldPosition;
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.cambium.mycelial_node");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int syncId, Inventory playerInventory, Player player) {
        return new com.warpgames.cambium.screen.MycelialNodeMenu(syncId, playerInventory, this.worldPosition);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, MycelialNodeBlockEntity entity) {
        if (level.isClientSide())
            return;

        entity.scanTimer++;
        if (entity.scanTimer >= 40) {
            entity.scanNetwork(level, pos);
            entity.scanTimer = 0;
        }
    }

    private void scanNetwork(Level level, BlockPos startPos) {
        Set<BlockPos> visited = new HashSet<>();
        Queue<BlockPos> queue = new LinkedList<>();

        this.connectedInventories.clear();
        this.networkItems.clear();

        queue.add(startPos);
        visited.add(startPos);

        int maxBlocksSearch = 2000;
        int searched = 0;

        while (!queue.isEmpty() && searched < maxBlocksSearch) {
            BlockPos current = queue.poll();
            searched++;

            BlockState currentState = level.getBlockState(current);
            boolean currentIsStrand = currentState.is(ModBlocks.MYCELIAL_STRAND);

            for (Direction dir : Direction.values()) {
                if (currentIsStrand) {
                    StrandConnection conn = currentState.getValue(MycelialStrandBlock.getProperty(dir));
                    if (conn != StrandConnection.CONNECTED) {
                        continue;
                    }
                }

                BlockPos neighborPos = current.relative(dir);
                if (visited.contains(neighborPos))
                    continue;

                BlockState neighborState = level.getBlockState(neighborPos);

                if (neighborState.is(ModBlocks.MYCELIAL_STRAND)) {
                    visited.add(neighborPos);
                    queue.add(neighborPos);
                } else {
                    BlockEntity be = level.getBlockEntity(neighborPos);
                    if (be instanceof Container && !(be instanceof MycelialNodeBlockEntity)) {
                        visited.add(neighborPos);
                        this.connectedInventories.add(neighborPos);
                    }
                }
            }
        }

        aggregateItems(level);
    }

    private void aggregateItems(Level level) {
        Map<String, ItemStack> consolidatedMap = new HashMap<>();

        for (BlockPos invPos : connectedInventories) {
            BlockEntity be = level.getBlockEntity(invPos);
            if (be instanceof Container container) {
                int[] slots = (container instanceof WorldlyContainer w) ? w.getSlotsForFace(Direction.UP)
                        : IntStream.range(0, container.getContainerSize()).toArray();

                for (int i : slots) {
                    ItemStack stack = container.getItem(i);
                    if (!stack.isEmpty()) {
                        String key = stack.getItem().toString() + stack.getComponents().toString();
                        if (consolidatedMap.containsKey(key)) {
                            ItemStack existing = consolidatedMap.get(key);
                            existing.grow(stack.getCount());
                        } else {
                            consolidatedMap.put(key, stack.copy());
                        }
                    }
                }
            }
        }

        this.networkItems.addAll(consolidatedMap.values());
    }

    public List<ItemStack> getNetworkItems() {
        return this.networkItems;
    }

    public ItemStack insertIntoNetwork(ItemStack stack) {
        if (stack.isEmpty() || this.level == null)
            return stack;

        ItemStack remainder = stack.copy();

        for (BlockPos invPos : connectedInventories) {
            BlockEntity be = this.level.getBlockEntity(invPos);
            if (be instanceof Container container) {
                remainder = insertItemHelper(container, remainder, true);
                if (remainder.isEmpty())
                    break;
            }
        }

        if (!remainder.isEmpty()) {
            for (BlockPos invPos : connectedInventories) {
                BlockEntity be = this.level.getBlockEntity(invPos);
                if (be instanceof Container container) {
                    remainder = insertItemHelper(container, remainder, false);
                    if (remainder.isEmpty())
                        break;
                }
            }
        }

        if (remainder.getCount() != stack.getCount()) {
            scanNetwork(this.level, this.worldPosition);
        }

        return remainder;
    }

    private ItemStack insertItemHelper(Container destination, ItemStack stack, boolean onlyMerge) {
        if (stack.isEmpty())
            return stack;
        int[] slots = (destination instanceof WorldlyContainer w) ? w.getSlotsForFace(Direction.UP)
                : IntStream.range(0, destination.getContainerSize()).toArray();

        if (onlyMerge) {
            for (int i : slots) {
                ItemStack slotStack = destination.getItem(i);
                if (ItemStack.isSameItemSameComponents(slotStack, stack)) {
                    int limit = Math.min(destination.getMaxStackSize(stack), slotStack.getMaxStackSize());
                    int space = limit - slotStack.getCount();
                    if (space > 0) {
                        int move = Math.min(space, stack.getCount());
                        slotStack.grow(move);
                        stack.shrink(move);
                        destination.setChanged();
                        if (stack.isEmpty())
                            return ItemStack.EMPTY;
                    }
                }
            }
        } else {
            for (int i : slots) {
                if (destination.getItem(i).isEmpty()) {
                    destination.setItem(i, stack.copy());
                    stack.setCount(0);
                    destination.setChanged();
                    return ItemStack.EMPTY;
                }
            }
        }
        return stack;
    }

    public ItemStack extractFromNetwork(ItemStack requested, int amount) {
        if (this.level == null)
            return ItemStack.EMPTY;

        int extractedAmount = 0;
        ItemStack extractedStack = ItemStack.EMPTY;

        for (BlockPos invPos : connectedInventories) {
            BlockEntity be = this.level.getBlockEntity(invPos);
            if (be instanceof Container container) {
                int[] slots = (container instanceof WorldlyContainer w) ? w.getSlotsForFace(Direction.UP)
                        : IntStream.range(0, container.getContainerSize()).toArray();

                for (int i : slots) {
                    ItemStack slotStack = container.getItem(i);
                    if (ItemStack.isSameItemSameComponents(slotStack, requested)) {
                        int take = Math.min(amount - extractedAmount, slotStack.getCount());

                        if (extractedStack.isEmpty()) {
                            extractedStack = slotStack.copy();
                            extractedStack.setCount(take);
                        } else {
                            extractedStack.grow(take);
                        }

                        slotStack.shrink(take);
                        extractedAmount += take;
                        container.setChanged();

                        if (extractedAmount >= amount) {
                            scanNetwork(this.level, this.worldPosition);
                            return extractedStack;
                        }
                    }
                }
            }
        }

        if (extractedAmount > 0) {
            scanNetwork(this.level, this.worldPosition);
        }

        return extractedStack;
    }
}
