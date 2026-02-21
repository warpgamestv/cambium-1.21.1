package com.warpgames.cambium.registry;

import com.warpgames.cambium.Cambium;
import com.warpgames.cambium.block.entity.MycelialNodeBlockEntity;
import com.warpgames.cambium.network.MycelialNetworkExtractPayload;
import com.warpgames.cambium.network.MycelialNetworkInsertPayload;
import com.warpgames.cambium.network.MycelialNetworkSyncPayload;
import com.warpgames.cambium.screen.MycelialNodeMenu;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;

public class ModNetworking {

    public static void registerPayloads() {
        Cambium.LOGGER.info("Registering Network Payloads for " + Cambium.MOD_ID);

        PayloadTypeRegistry.playS2C().register(MycelialNetworkSyncPayload.ID, MycelialNetworkSyncPayload.CODEC);
        PayloadTypeRegistry.playC2S().register(MycelialNetworkExtractPayload.ID, MycelialNetworkExtractPayload.CODEC);
        PayloadTypeRegistry.playC2S().register(MycelialNetworkInsertPayload.ID, MycelialNetworkInsertPayload.CODEC);
        PayloadTypeRegistry.playC2S().register(com.warpgames.cambium.network.MycelialNetworkRequestSyncPayload.ID,
                com.warpgames.cambium.network.MycelialNetworkRequestSyncPayload.CODEC);
    }

    public static void registerServerReceivers() {
        ServerPlayNetworking.registerGlobalReceiver(MycelialNetworkExtractPayload.ID, (payload, context) -> {
            ServerPlayer player = context.player();
            if (player.containerMenu instanceof MycelialNodeMenu menu) {
                if (menu.blockPos.equals(payload.nodePos())) {
                    context.server().execute(() -> {
                        BlockEntity be = player.level().getBlockEntity(payload.nodePos());
                        if (be instanceof MycelialNodeBlockEntity nodeBE) {
                            ItemStack extracted = nodeBE.extractFromNetwork(payload.requestedItem(), payload.amount());
                            if (!extracted.isEmpty()) {
                                if (!player.getInventory().add(extracted)) {
                                    player.level().addFreshEntity(new ItemEntity(player.level(), player.getX(),
                                            player.getY(), player.getZ(), extracted));
                                }
                                ServerPlayNetworking.send(player,
                                        new MycelialNetworkSyncPayload(nodeBE.getNetworkItems()));
                            }
                        }
                    });
                }
            }
        });

        ServerPlayNetworking.registerGlobalReceiver(MycelialNetworkInsertPayload.ID, (payload, context) -> {
            ServerPlayer player = context.player();
            if (player.containerMenu instanceof MycelialNodeMenu menu) {
                if (menu.blockPos.equals(payload.nodePos())) {
                    context.server().execute(() -> {
                        BlockEntity be = player.level().getBlockEntity(payload.nodePos());
                        if (be instanceof MycelialNodeBlockEntity nodeBE) {
                            ItemStack toInsert = player.getInventory().getItem(payload.playerSlotIndex());
                            if (!toInsert.isEmpty()) {
                                ItemStack remainder = nodeBE.insertIntoNetwork(toInsert);
                                player.getInventory().setItem(payload.playerSlotIndex(), remainder);
                                player.getInventory().setChanged();
                                ServerPlayNetworking.send(player,
                                        new MycelialNetworkSyncPayload(nodeBE.getNetworkItems()));
                            }
                        }
                    });
                }
            }
        });

        ServerPlayNetworking.registerGlobalReceiver(com.warpgames.cambium.network.MycelialNetworkRequestSyncPayload.ID,
                (payload, context) -> {
                    ServerPlayer player = context.player();
                    if (player.containerMenu instanceof MycelialNodeMenu menu) {
                        if (menu.blockPos.equals(payload.nodePos())) {
                            context.server().execute(() -> {
                                BlockEntity be = player.level().getBlockEntity(payload.nodePos());
                                if (be instanceof MycelialNodeBlockEntity nodeBE) {
                                    ServerPlayNetworking.send(player,
                                            new MycelialNetworkSyncPayload(nodeBE.getNetworkItems()));
                                }
                            });
                        }
                    }
                });
    }
}
