package com.warpgames.cambium.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.warpgames.cambium.block.entity.PhloemDuctBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class PhloemDuctRenderer implements BlockEntityRenderer<PhloemDuctBlockEntity> {

    public PhloemDuctRenderer(BlockEntityRendererProvider.Context context) {
        // Context is required by the constructor signature, even if unused.
    }

    @Override
    public void render(PhloemDuctBlockEntity entity, float tickDelta, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        // 1. Get the item
        ItemStack itemStack = entity.getItem(0);
        if (itemStack.isEmpty()) return;

        poseStack.pushPose();

        // 2. Center it
        poseStack.translate(0.5, 0.5, 0.5);

        // 3. Scale it down
        poseStack.scale(0.4f, 0.4f, 0.4f);

        // 4. Spin Logic
        if (entity.getLevel() != null) {
            long time = entity.getLevel().getGameTime();
            float spin = (time + tickDelta) * 4;
            poseStack.mulPose(Axis.YP.rotationDegrees(spin));
        }

        // 5. Render
        Minecraft.getInstance().getItemRenderer().renderStatic(
                itemStack,
                ItemDisplayContext.FIXED,
                packedLight, // Use the block's light level
                packedOverlay,
                poseStack,
                bufferSource,
                entity.getLevel(),
                0
        );

        poseStack.popPose();
    }
}