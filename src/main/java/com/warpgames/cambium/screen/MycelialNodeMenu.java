package com.warpgames.cambium.screen;

import com.warpgames.cambium.registry.ModScreenHandlers;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class MycelialNodeMenu extends AbstractContainerMenu {
    public final BlockPos blockPos;

    // Client-side constructor
    public MycelialNodeMenu(int syncId, Inventory playerInventory, net.minecraft.network.FriendlyByteBuf buf) {
        this(syncId, playerInventory, buf.readBlockPos());
    }

    // Server-side constructor
    public MycelialNodeMenu(int syncId, Inventory playerInventory, BlockPos blockPos) {
        super(ModScreenHandlers.MYCELIAL_NODE_MENU, syncId);
        this.blockPos = blockPos;

        // Add player inventory (3x9)
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 140 + i * 18));
            }
        }

        // Add player hotbar (1x9)
        for (int k = 0; k < 9; ++k) {
            this.addSlot(new Slot(playerInventory, k, 8 + k * 18, 198));
        }
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        Slot slot = this.slots.get(index);

        if (slot != null && slot.hasItem()) {
            ItemStack stackInSlot = slot.getItem();

            if (!player.level().isClientSide()) {
                net.minecraft.world.level.block.entity.BlockEntity be = player.level().getBlockEntity(this.blockPos);
                if (be instanceof com.warpgames.cambium.block.entity.MycelialNodeBlockEntity nodeBE) {
                    ItemStack remainder = nodeBE.insertIntoNetwork(stackInSlot.copy());

                    if (remainder.getCount() != stackInSlot.getCount()) {
                        slot.set(remainder);
                        slot.setChanged();

                        net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking.send(
                                (net.minecraft.server.level.ServerPlayer) player,
                                new com.warpgames.cambium.network.MycelialNetworkSyncPayload(nodeBE.getNetworkItems()));

                        return stackInSlot.copy();
                    }
                }
            }
        }

        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player player) {
        return player.distanceToSqr(blockPos.getX() + 0.5D, blockPos.getY() + 0.5D, blockPos.getZ() + 0.5D) <= 64.0D;
    }
}
