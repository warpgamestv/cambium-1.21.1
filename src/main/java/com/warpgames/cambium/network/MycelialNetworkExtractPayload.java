package com.warpgames.cambium.network;

import com.warpgames.cambium.Cambium;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public record MycelialNetworkExtractPayload(BlockPos nodePos, ItemStack requestedItem, int amount)
        implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<MycelialNetworkExtractPayload> ID = new CustomPacketPayload.Type<>(
            ResourceLocation.fromNamespaceAndPath(Cambium.MOD_ID, "mycelial_extract"));

    public static final StreamCodec<RegistryFriendlyByteBuf, MycelialNetworkExtractPayload> CODEC = StreamCodec
            .composite(
                    BlockPos.STREAM_CODEC, MycelialNetworkExtractPayload::nodePos,
                    ItemStack.STREAM_CODEC, MycelialNetworkExtractPayload::requestedItem,
                    net.minecraft.network.codec.ByteBufCodecs.VAR_INT, MycelialNetworkExtractPayload::amount,
                    MycelialNetworkExtractPayload::new);

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return ID;
    }
}
