package com.warpgames.cambium.network;

import com.warpgames.cambium.Cambium;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record MycelialNetworkInsertPayload(BlockPos nodePos, int playerSlotIndex) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<MycelialNetworkInsertPayload> ID = new CustomPacketPayload.Type<>(
            ResourceLocation.fromNamespaceAndPath(Cambium.MOD_ID, "mycelial_insert"));

    public static final StreamCodec<RegistryFriendlyByteBuf, MycelialNetworkInsertPayload> CODEC = StreamCodec
            .composite(
                    BlockPos.STREAM_CODEC, MycelialNetworkInsertPayload::nodePos,
                    net.minecraft.network.codec.ByteBufCodecs.VAR_INT, MycelialNetworkInsertPayload::playerSlotIndex,
                    MycelialNetworkInsertPayload::new);

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return ID;
    }
}
