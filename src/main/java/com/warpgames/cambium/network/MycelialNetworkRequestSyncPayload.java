package com.warpgames.cambium.network;

import com.warpgames.cambium.Cambium;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record MycelialNetworkRequestSyncPayload(BlockPos nodePos) implements CustomPacketPayload {

    public static final Type<MycelialNetworkRequestSyncPayload> ID = new Type<>(
            ResourceLocation.fromNamespaceAndPath(Cambium.MOD_ID, "mycelial_network_request_sync"));

    public static final StreamCodec<FriendlyByteBuf, MycelialNetworkRequestSyncPayload> CODEC = StreamCodec.of(
            (buf, payload) -> {
                buf.writeBlockPos(payload.nodePos());
            },
            buf -> new MycelialNetworkRequestSyncPayload(
                    buf.readBlockPos()));

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }
}
