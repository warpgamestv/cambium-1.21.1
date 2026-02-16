package com.warpgames.cambium.block.entity;

import com.warpgames.cambium.block.transport.DuctConnection;
import com.warpgames.cambium.block.transport.PhloemDuctBlock;
import com.warpgames.cambium.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup; // Import 1
import net.minecraft.nbt.CompoundTag;   // Import 2
import net.minecraft.world.Container;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import org.jetbrains.annotations.Nullable;

import java.util.stream.IntStream;

public class PhloemDuctBlockEntity extends BlockEntity implements WorldlyContainer {

    private ItemStack heldItem = ItemStack.EMPTY;
    private int cooldown = 0;
    private Direction lastInputDir = null;

    public PhloemDuctBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.PHLOEM_DUCT, pos, blockState);
    }

    // [KEEP YOUR TICK / TRANSFER LOGIC HERE - IT IS UNCHANGED]
    public static void serverTick(Level level, BlockPos pos, BlockState state, PhloemDuctBlockEntity entity) {
        if (entity.cooldown > 0) { entity.cooldown--; return; }
        entity.cooldown = 8;
        if (entity.heldItem.isEmpty()) entity.tryExtract(level, pos, state);
        if (!entity.heldItem.isEmpty()) entity.tryTransfer(level, pos, state);
    }
    // ... [Rest of logic omitted for brevity, keep it identical to before] ...

    private void tryExtract(Level level, BlockPos pos, BlockState state) {
        // ... [Keep existing logic] ...
        for (Direction dir : Direction.values()) {
            EnumProperty<DuctConnection> prop = getPropertyForFace(dir);
            if (state.getValue(prop) == DuctConnection.EXTRACT) {
                BlockPos neighborPos = pos.relative(dir);
                BlockEntity neighborBE = level.getBlockEntity(neighborPos);

                if (neighborBE instanceof Container neighborInv) {
                    ItemStack extracted = extractItem(neighborInv, dir.getOpposite());
                    if (!extracted.isEmpty()) {
                        this.heldItem = extracted;
                        this.lastInputDir = dir;
                        this.setChanged();
                        return;
                    }
                }
            }
        }
    }
    private void tryTransfer(Level level, BlockPos pos, BlockState state) {
        // ... [Keep existing logic] ...
        for (Direction dir : Direction.values()) {
            if (dir == this.lastInputDir) continue;

            EnumProperty<DuctConnection> prop = getPropertyForFace(dir);
            DuctConnection connection = state.getValue(prop);

            if (connection == DuctConnection.NONE || connection == DuctConnection.EXTRACT) continue;

            BlockPos neighborPos = pos.relative(dir);
            BlockEntity neighborBE = level.getBlockEntity(neighborPos);

            if (neighborBE instanceof PhloemDuctBlockEntity targetPipe) {
                if (targetPipe.isEmpty()) {
                    targetPipe.heldItem = this.heldItem.split(1);
                    targetPipe.lastInputDir = dir.getOpposite();
                    targetPipe.setChanged();
                    this.setChanged();
                    this.lastInputDir = null;
                    return;
                }
            }
            else if (neighborBE instanceof Container target) {
                ItemStack remaining = insertItem(target, this.heldItem.copy(), dir.getOpposite());
                if (remaining.getCount() != this.heldItem.getCount()) {
                    this.heldItem = remaining;
                    this.setChanged();
                    if (this.heldItem.isEmpty()) this.lastInputDir = null;
                    return;
                }
            }
        }
        if (!this.heldItem.isEmpty()) {
            this.lastInputDir = null;
        }
    }

    // --- HELPER METHODS ---
    // [Keep existing insertItem/extractItem methods]
    private static ItemStack extractItem(Container source, Direction side) {
        int[] slots;
        if (source instanceof WorldlyContainer worldly) {
            slots = worldly.getSlotsForFace(side);
        } else {
            slots = IntStream.range(0, source.getContainerSize()).toArray();
        }

        for (int i : slots) {
            ItemStack stack = source.getItem(i);
            if (stack.isEmpty()) continue;

            if (source instanceof WorldlyContainer worldly && !worldly.canTakeItemThroughFace(i, stack, side)) {
                continue;
            }

            ItemStack result = stack.split(1);
            source.setChanged();
            return result;
        }
        return ItemStack.EMPTY;
    }

    private static ItemStack insertItem(Container destination, ItemStack stack, Direction side) {
        int[] slots;
        if (destination instanceof WorldlyContainer worldly) {
            slots = worldly.getSlotsForFace(side);
        } else {
            slots = IntStream.range(0, destination.getContainerSize()).toArray();
        }

        for (int i : slots) {
            ItemStack slotStack = destination.getItem(i);

            if (!destination.canPlaceItem(i, stack)) continue;
            if (destination instanceof WorldlyContainer worldly && !worldly.canPlaceItemThroughFace(i, stack, side)) {
                continue;
            }

            if (slotStack.isEmpty()) {
                destination.setItem(i, stack);
                destination.setChanged();
                return ItemStack.EMPTY;
            } else if (ItemStack.isSameItemSameComponents(slotStack, stack)) {
                int space = slotStack.getMaxStackSize() - slotStack.getCount();
                int toMove = Math.min(space, stack.getCount());

                if (toMove > 0) {
                    slotStack.grow(toMove);
                    stack.shrink(toMove);
                    destination.setChanged();
                    return stack;
                }
            }
        }
        return stack;
    }

    private EnumProperty<DuctConnection> getPropertyForFace(Direction face) {
        return switch (face) {
            case NORTH -> PhloemDuctBlock.NORTH;
            case SOUTH -> PhloemDuctBlock.SOUTH;
            case EAST -> PhloemDuctBlock.EAST;
            case WEST -> PhloemDuctBlock.WEST;
            case UP -> PhloemDuctBlock.UP;
            case DOWN -> PhloemDuctBlock.DOWN;
        };
    }


    // --- 1.21.1 SAVE/LOAD LOGIC ---

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.saveAdditional(tag, provider);
        if (!heldItem.isEmpty()) {
            // In 1.21.1, we save to a Tag and put it inside the main CompoundTag
            tag.put("HeldItem", heldItem.save(provider));
        }
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.loadAdditional(tag, provider);
        if (tag.contains("HeldItem")) {
            // In 1.21.1, we use parseOptional to load from NBT
            this.heldItem = ItemStack.parseOptional(provider, tag.getCompound("HeldItem"));
        }
    }

    // --- CONTAINER METHODS (Unchanged) ---
    @Override public int getContainerSize() { return 1; }
    @Override public boolean isEmpty() { return heldItem.isEmpty(); }
    @Override public ItemStack getItem(int slot) { return heldItem; }
    @Override public ItemStack removeItem(int slot, int amount) { ItemStack split = heldItem.split(amount); if (heldItem.isEmpty()) heldItem = ItemStack.EMPTY; setChanged(); return split; }
    @Override public ItemStack removeItemNoUpdate(int slot) { ItemStack stack = heldItem; heldItem = ItemStack.EMPTY; return stack; }
    @Override public void setItem(int slot, ItemStack stack) { heldItem = stack; setChanged(); }
    @Override public boolean stillValid(Player player) { return false; }
    @Override public void clearContent() { heldItem = ItemStack.EMPTY; setChanged(); }
    @Override public int[] getSlotsForFace(Direction side) { return new int[]{0}; }
    @Override public boolean canPlaceItemThroughFace(int slot, ItemStack stack, @Nullable Direction dir) { return heldItem.isEmpty(); }
    @Override public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction dir) { return true; }
}