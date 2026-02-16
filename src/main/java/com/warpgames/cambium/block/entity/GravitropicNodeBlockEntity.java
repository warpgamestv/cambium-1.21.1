package com.warpgames.cambium.block.entity;

import com.warpgames.cambium.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class GravitropicNodeBlockEntity extends BlockEntity implements ImplementedInventory {

    private final net.minecraft.core.NonNullList<ItemStack> inventory = net.minecraft.core.NonNullList.withSize(9, ItemStack.EMPTY);
    private static final int RANGE = 5; // 11x11 area
    private static final double SPEED = 0.05; // Pull speed

    public GravitropicNodeBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.GRAVITROPIC_NODE_BE, pos, state);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, GravitropicNodeBlockEntity entity) {
        if (level.isClientSide()) return;

        // 1. Define the Scan Area
        AABB searchArea = new AABB(pos)
                .inflate(RANGE, 0, RANGE)  // Horizontal reach (Width)
                .expandTowards(0, 8.0, 0)  // Vertical reach UP (Height)
                .expandTowards(0, -2.0, 0);

        // 2. Find Items
        List<ItemEntity> items = level.getEntitiesOfClass(ItemEntity.class, searchArea);

        for (ItemEntity itemEntity : items) {
            if (itemEntity.isRemoved() || itemEntity.getItem().isEmpty()) continue;

            Vec3 itemPos = itemEntity.position();
            Vec3 center = Vec3.atCenterOf(pos);
            double distanceSq = itemPos.distanceToSqr(center);

            // 3. Logic
            if (distanceSq < 1.0) {
                // CLOSE RANGE: Insert
                ItemStack stack = itemEntity.getItem();
                ItemStack remainder = entity.addItem(stack); // Use our custom helper

                if (remainder.isEmpty()) {
                    itemEntity.discard();
                } else {
                    itemEntity.setItem(remainder);
                }

            } else {
                // LONG RANGE: Pull
                Vec3 direction = center.subtract(itemPos).normalize().scale(SPEED);
                if (itemPos.y < center.y - 0.2) {
                    direction = direction.add(0, 0.05, 0);
                }

                Vec3 currentMotion = itemEntity.getDeltaMovement();
                // Apply drag (0.95) to prevent orbiting
                itemEntity.setDeltaMovement(currentMotion.add(direction).scale(0.95));
            }
        }
    }

    // --- Custom Helper to Insert Items ---
    public ItemStack addItem(ItemStack stack) {
        ItemStack copy = stack.copy();
        boolean didChange = false; // Track if we touched the inventory

        // 1. Try to merge with existing stacks
        for (ItemStack slot : inventory) {
            if (ItemStack.isSameItemSameComponents(slot, copy)) {
                int limit = slot.getMaxStackSize();
                int space = limit - slot.getCount();

                if (space > 0) {
                    int add = Math.min(copy.getCount(), space);
                    slot.grow(add);
                    copy.shrink(add);
                    didChange = true; // We modified a slot

                    if (copy.isEmpty()) break; // Done merging
                }
            }
        }

        // 2. If we still have items, try to put in empty slots
        if (!copy.isEmpty()) {
            for (int i = 0; i < inventory.size(); i++) {
                if (inventory.get(i).isEmpty()) {
                    inventory.set(i, copy.copy());
                    copy.setCount(0); // All gone
                    didChange = true; // We filled a slot
                    break;
                }
            }
        }

        // 3. IMPORTANT: If anything changed, mark the block as dirty so it saves!
        if (didChange) {
            setChanged();
        }

        return copy;
    }

    // --- Inventory Boilerplate ---

    @Override
    public net.minecraft.core.NonNullList<ItemStack> getItems() {
        return inventory;
    }

    @Override
    public void setChanged() {
        super.setChanged();
    }

    // 1.21 Save/Load Logic (Requires 2 arguments: Tag + Registry)
    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.saveAdditional(tag, provider);
        // Save the inventory to the NBT Tag
        ContainerHelper.saveAllItems(tag, this.inventory, provider);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.loadAdditional(tag, provider);
        // Load the inventory from the NBT Tag
        ContainerHelper.loadAllItems(tag, this.inventory, provider);
    }
}