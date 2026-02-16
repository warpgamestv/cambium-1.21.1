package com.warpgames.cambium.menu;

import com.warpgames.cambium.registry.ModBlocks;
import com.warpgames.cambium.registry.ModScreenHandlers;
import com.warpgames.cambium.registry.ModTags;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;

public class SolarConcentratorMenu extends AbstractContainerMenu {
    private final ContainerData data;
    private final ContainerLevelAccess access;
    private final Container container;

    // 1. Client Constructor (Matches SolarDigester)
    public SolarConcentratorMenu(int syncId, Inventory playerInventory, BlockPos pos) {
        this(syncId, playerInventory, ContainerLevelAccess.create(playerInventory.player.level(), pos), new SimpleContainerData(2), new SimpleContainer(3));
    }

    // 2. Server Constructor
    public SolarConcentratorMenu(int syncId, Inventory playerInventory, ContainerLevelAccess access, ContainerData data, Container container) {
        super(ModScreenHandlers.SOLAR_CONCENTRATOR_SCREEN_HANDLER, syncId);
        checkContainerSize(container, 3);
        this.access = access;
        this.container = container;
        this.data = data;
        container.startOpen(playerInventory.player);
        addDataSlots(data);

        // --- SLOTS ---
        // Input
        this.addSlot(new Slot(container, 0, 41 + 1, 41 + 1));
        // Output
        this.addSlot(new Slot(container, 1, 121 + 1, 41 + 1) {
            @Override public boolean mayPlace(ItemStack stack) { return false; }
        });
        // Lens
        this.addSlot(new Slot(container, 2, 81 + 1, 16 + 1) {
             @Override
             public boolean mayPlace(ItemStack stack) {
                 return stack.is(ModTags.Items.LENS);
             }

             @Override
             public int getMaxStackSize() {
                 return 1;
             }
         });

        addPlayerInventory(playerInventory);
        addPlayerHotbar(playerInventory);
    }

    @Override
    public boolean stillValid(Player player) {
        // Secure validation using ContainerLevelAccess
        return stillValid(access, player, ModBlocks.SOLAR_CONCENTRATOR);
    }

    // ... (quickMoveStack, isCrafting, getScaledProgress kept same as before) ...

    public boolean isCrafting() { return data.get(0) > 0; }

    public int getScaledProgress() {
        int progress = data.get(0);
        int maxProgress = data.get(1);
        int arrowSize = 24;
        return maxProgress != 0 && progress != 0 ? progress * arrowSize / maxProgress : 0;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int invSlot) {
        ItemStack newStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(invSlot);
        if (slot != null && slot.hasItem()) {
            ItemStack originalStack = slot.getItem();
            newStack = originalStack.copy();
            if (invSlot < 3) {
                if (!this.moveItemStackTo(originalStack, 3, this.slots.size(), true)) return ItemStack.EMPTY;
            } else if (!this.moveItemStackTo(originalStack, 0, 3, false)) return ItemStack.EMPTY;
            if (originalStack.isEmpty()) slot.set(ItemStack.EMPTY); else slot.setChanged();
        }
        return newStack;
    }

    private void addPlayerInventory(Inventory playerInventory) {
        for (int i = 0; i < 3; ++i) {
            for (int l = 0; l < 9; ++l) {
                this.addSlot(new Slot(playerInventory, l + i * 9 + 9, 8 + l * 18, 84 + i * 18));
            }
        }
    }

    private void addPlayerHotbar(Inventory playerInventory) {
        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 142));
        }
    }
}