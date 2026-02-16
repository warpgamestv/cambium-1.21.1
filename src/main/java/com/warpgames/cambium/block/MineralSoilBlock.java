package com.warpgames.cambium.block;

import com.mojang.serialization.MapCodec;
import com.warpgames.cambium.block.entity.MineralSoilBlockEntity;
import com.warpgames.cambium.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult; // IMPORT THIS!
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public class MineralSoilBlock extends BaseEntityBlock {

    public static final MapCodec<MineralSoilBlock> CODEC = simpleCodec(MineralSoilBlock::new);

    public MineralSoilBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new MineralSoilBlockEntity(pos, state);
    }

    // --- FIX: USE ItemInteractionResult ---
    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        // 1. Check if the item in hand is Organic Ash
        if (stack.is(ModItems.ORGANIC_ASH)) {

            // 2. Run logic (Server Side Only)
            if (!level.isClientSide()) {
                if (level.getBlockEntity(pos) instanceof MineralSoilBlockEntity soilEntity) {

                    soilEntity.addCharge(10);
                    stack.shrink(1);

                    level.playSound(null, pos, SoundEvents.BONE_MEAL_USE, SoundSource.BLOCKS, 1.0F, 1.0F);
                    player.displayClientMessage(net.minecraft.network.chat.Component.literal("Soil Charge: " + soilEntity.getCharge()), true);
                }
            }

            // Return the specific ITEM success result
            return ItemInteractionResult.SUCCESS;
        }

        // Return PASS equivalent for Items
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }
}