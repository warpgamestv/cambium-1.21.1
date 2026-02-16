package com.warpgames.cambium.menu;

import com.warpgames.cambium.registry.ModBlocks;
import com.warpgames.cambium.registry.ModScreenHandlers;
import com.warpgames.cambium.registry.ModTags;
import net.minecraft.core.BlockPos; // Import BlockPos
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;

public class SolarDigesterMenu extends AbstractContainerMenu {
    private final ContainerData data;
    private final ContainerLevelAccess access;
    private final Container container;

    // Client Constructor
    public SolarDigesterMenu(int syncId, Inventory playerInventory, BlockPos pos) {
        this(syncId, playerInventory, ContainerLevelAccess.create(playerInventory.player.level(), pos), new SimpleContainerData(2), new SimpleContainer(4));
    }

    // Server Constructor
    public SolarDigesterMenu(int syncId, Inventory playerInventory, ContainerLevelAccess access, ContainerData data, Container container) {
        super(ModScreenHandlers.SOLAR_DIGESTER_MENU, syncId);
        this.access = access;
        this.data = data;
        this.container = container;
        checkContainerSize(container, 4);
        container.startOpen(playerInventory.player);

        addDataSlots(data);

        // --- MACHINE SLOTS ---
        // 0: Input
        this.addSlot(new Slot(container, 0, 41 + 1, 41 + 1));
        // 1: Output
        this.addSlot(new Slot(container, 1, 121 + 1, 31 + 1) {
            @Override public boolean mayPlace(ItemStack stack) { return false; }
        });
        // 2: Byproduct
        this.addSlot(new Slot(container, 2, 121 + 1, 51 + 1) {
            @Override public boolean mayPlace(ItemStack stack) { return false; }
        });
        // 3: Lens
        this.addSlot(new Slot(container, 3, 81 + 1, 16 + 1){
            @Override
            public boolean mayPlace(ItemStack stack) {
                return stack.is(ModTags.Items.LENS);
            }
            @Override
            public int getMaxStackSize() {
                return 1;
            }
        });

        // Player Inventory
        addPlayerInventory(playerInventory);
        addPlayerHotbar(playerInventory);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(access, player, ModBlocks.SOLAR_DIGESTER);
    }
    @Override
    public void removed(Player player) {
        super.removed(player);
        this.container.stopOpen(player);
    }

    private void addPlayerInventory(Inventory playerInventory) {
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }
    }

    private void addPlayerHotbar(Inventory playerInventory) {
        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 142));
        }
    }
    public boolean isCrafting() {
        return data.get(0) > 0;
    }

    public int getScaledProgress() {
        int progress = this.data.get(0);
        int maxProgress = this.data.get(1);
        int arrowPixelSize = 24;

        if (maxProgress == 0 || progress == 0) {
            return 0;
        }

        return progress * arrowPixelSize / maxProgress;
    }
}