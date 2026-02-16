package com.warpgames.cambium.block.entity;

import com.warpgames.cambium.registry.ModBlockEntities;
import com.warpgames.cambium.registry.ModItems;
import com.warpgames.cambium.menu.SolarConcentratorMenu;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup; // Import 1
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;   // Import 2
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class SolarConcentratorBlockEntity extends BlockEntity implements ExtendedScreenHandlerFactory<BlockPos>, WorldlyContainer {

    private final NonNullList<ItemStack> inventory = NonNullList.withSize(3, ItemStack.EMPTY);
    private static final int INPUT_SLOT = 0;
    private static final int OUTPUT_SLOT = 1;
    private static final int LENS_SLOT = 2;

    protected final ContainerData data;
    private int progress = 0;
    private int maxProgress = 72;

    public SolarConcentratorBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.SOLAR_CONCENTRATOR_BE, pos, state);
        // [KEEP DATA IMPLEMENTATION]
        this.data = new ContainerData() {
            @Override
            public int get(int index) {
                return switch (index) {
                    case 0 -> SolarConcentratorBlockEntity.this.progress;
                    case 1 -> SolarConcentratorBlockEntity.this.maxProgress;
                    default -> 0;
                };
            }
            @Override
            public void set(int index, int value) {
                switch (index) {
                    case 0 -> SolarConcentratorBlockEntity.this.progress = value;
                    case 1 -> SolarConcentratorBlockEntity.this.maxProgress = value;
                }
            }
            @Override
            public int getCount() { return 2; }
        };
    }

    // --- 1.21.1 SAVE/LOAD LOGIC ---

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.saveAdditional(tag, provider);
        tag.putInt("solar_concentrator.progress", progress);
        tag.putInt("solar_concentrator.max_progress", maxProgress);
        // ContainerHelper.saveAllItems takes (Tag, Inventory, Provider) in 1.21.1
        ContainerHelper.saveAllItems(tag, this.inventory, provider);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.loadAdditional(tag, provider);
        this.progress = tag.getInt("solar_concentrator.progress");
        this.maxProgress = tag.getInt("solar_concentrator.max_progress");
        // ContainerHelper.loadAllItems takes (Tag, Inventory, Provider)
        ContainerHelper.loadAllItems(tag, this.inventory, provider);
    }

    // [Paste remainder of standard methods: tick, getSlots, etc.]
    // --- AUTOMATION RULES (WorldlyContainer) ---
    @Override
    public int[] getSlotsForFace(Direction side) {
        if (side == Direction.DOWN) {
            return new int[]{OUTPUT_SLOT};
        } else {
            return new int[]{INPUT_SLOT};
        }
    }

    @Override
    public boolean canPlaceItemThroughFace(int slot, ItemStack stack, @Nullable Direction dir) {
        return slot == INPUT_SLOT;
    }

    @Override
    public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction dir) {
        return slot == OUTPUT_SLOT;
    }

    // --- REST OF THE FILE (Unchanged) ---
    @Override
    public BlockPos getScreenOpeningData(ServerPlayer player) {
        return this.getBlockPos();
    }

    @Override
    public Component getDisplayName() {
        return Component.literal("Solar Concentrator");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int syncId, Inventory playerInventory, Player player) {
        return new SolarConcentratorMenu(syncId, playerInventory, ContainerLevelAccess.create(level, this.getBlockPos()), this.data, this);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, SolarConcentratorBlockEntity entity) {
        if (level.isClientSide()) return;

        boolean isDay = level.getDayTime() % 24000 < 12300;
        boolean isRaining = level.isRaining();
        boolean canSeeSky = level.canSeeSky(pos.above());

        if (isDay && !isRaining && canSeeSky && hasRecipe(entity)) {
            boolean hasLens = entity.inventory.get(LENS_SLOT).is(ModItems.SOLAR_LENS);
            int speed = hasLens ? 2 : 1;
            entity.progress += speed;

            if (entity.progress >= entity.maxProgress) {
                craftItem(entity);
            }
            setChanged(level, pos, state);
        } else {
            entity.resetProgress();
        }
    }

    private static void craftItem(SolarConcentratorBlockEntity entity) {
        Level level = entity.level;
        if (!(level instanceof ServerLevel serverLevel)) return;

        SingleRecipeInput input = new SingleRecipeInput(entity.getItem(INPUT_SLOT));
        Optional<RecipeHolder<SmeltingRecipe>> recipe = serverLevel.getRecipeManager()
                .getRecipeFor(RecipeType.SMELTING, input, level);
        if (recipe.isPresent()) {
            ItemStack result = recipe.get().value().assemble(input, level.registryAccess());
            ItemStack outputStack = entity.getItem(OUTPUT_SLOT);
            if (outputStack.isEmpty()) {
                entity.setItem(OUTPUT_SLOT, result.copy());
            } else if (outputStack.is(result.getItem())) {
                outputStack.grow(result.getCount());
            }
            entity.getItem(INPUT_SLOT).shrink(1);
            entity.resetProgress();
        }
    }

    private static boolean hasRecipe(SolarConcentratorBlockEntity entity) {
        Level level = entity.level;
        if (!(level instanceof ServerLevel serverLevel)) return false;

        SingleRecipeInput input = new SingleRecipeInput(entity.getItem(INPUT_SLOT));
        Optional<RecipeHolder<SmeltingRecipe>> recipe = serverLevel.getRecipeManager()
                .getRecipeFor(RecipeType.SMELTING, input, level);

        return recipe.isPresent() &&
                canOutputFit(entity, recipe.get().value().assemble(input, level.registryAccess()));
    }

    private static boolean canOutputFit(SolarConcentratorBlockEntity entity, ItemStack result) {
        ItemStack outputStack = entity.getItem(OUTPUT_SLOT);
        return outputStack.isEmpty() ||
                (outputStack.is(result.getItem()) && outputStack.getCount() + result.getCount() <= outputStack.getMaxStackSize());
    }

    private void resetProgress() { this.progress = 0; }

    @Override public int getContainerSize() { return inventory.size(); }
    @Override public boolean isEmpty() { return inventory.stream().allMatch(ItemStack::isEmpty); }
    @Override public ItemStack getItem(int slot) { return inventory.get(slot); }
    @Override public ItemStack removeItem(int slot, int amount) { return ContainerHelper.removeItem(inventory, slot, amount); }
    @Override public ItemStack removeItemNoUpdate(int slot) { return ContainerHelper.takeItem(inventory, slot); }
    @Override public void setItem(int slot, ItemStack stack) { inventory.set(slot, stack); setChanged(); }
    @Override public boolean stillValid(Player player) { return Container.stillValidBlockEntity(this, player); }
    @Override public void clearContent() { inventory.clear(); }
}