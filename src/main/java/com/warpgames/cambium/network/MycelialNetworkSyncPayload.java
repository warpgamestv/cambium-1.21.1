package com.warpgames.cambium.network;

import com.warpgames.cambium.Cambium;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public record MycelialNetworkSyncPayload(List<ItemStack> items) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<MycelialNetworkSyncPayload> ID = new CustomPacketPayload.Type<>(
            ResourceLocation.fromNamespaceAndPath(Cambium.MOD_ID, "mycelial_sync"));

    public static final StreamCodec<RegistryFriendlyByteBuf, MycelialNetworkSyncPayload> CODEC = StreamCodec.composite(
            ItemStack.OPTIONAL_LIST_STREAM_CODEC,
            MycelialNetworkSyncPayload::items,
            MycelialNetworkSyncPayload::new);

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return ID;
    }
}
