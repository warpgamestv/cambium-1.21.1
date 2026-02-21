package com.warpgames.cambium.block;

import com.mojang.serialization.MapCodec;
import com.warpgames.cambium.block.entity.MycelialNodeBlockEntity;
import com.warpgames.cambium.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public class MycelialNodeBlock extends BaseEntityBlock {
    public static final MapCodec<MycelialNodeBlock> CODEC = simpleCodec(MycelialNodeBlock::new);

    public MycelialNodeBlock(Properties properties) {
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

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player,
            BlockHitResult hitResult) {
        if (!level.isClientSide()) {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof MycelialNodeBlockEntity menuProvider) {
                player.openMenu(menuProvider);
            }
        }
        return InteractionResult.SUCCESS;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new MycelialNodeBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state,
            BlockEntityType<T> type) {
        return createTickerHelper(type, ModBlockEntities.MYCELIAL_NODE_BE, MycelialNodeBlockEntity::tick);
    }

    @Override
    public void animateTick(@org.jetbrains.annotations.NotNull BlockState state,
            @org.jetbrains.annotations.NotNull Level level, @org.jetbrains.annotations.NotNull BlockPos pos,
            @org.jetbrains.annotations.NotNull net.minecraft.util.RandomSource random) {
        if (random.nextInt(5) == 0) {
            double d = (double) pos.getX() + random.nextDouble();
            double e = (double) pos.getY() - 0.05D;
            double f = (double) pos.getZ() + random.nextDouble();
            level.addParticle(net.minecraft.core.particles.ParticleTypes.SPORE_BLOSSOM_AIR, d, e, f, 0.0D, 0.0D, 0.0D);

            if (random.nextInt(2) == 0) {
                level.addParticle(net.minecraft.core.particles.ParticleTypes.SPORE_BLOSSOM_AIR, d, e + 1.2D, f, 0.0D,
                        0.0D, 0.0D);
            }
        }
    }
}
