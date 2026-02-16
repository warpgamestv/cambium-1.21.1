package com.warpgames.cambium.block;

import com.mojang.serialization.MapCodec;
import com.warpgames.cambium.block.entity.MineralSoilBlockEntity;
import com.warpgames.cambium.block.entity.RootBlockEntity;
import com.warpgames.cambium.content.ResourceTree;
import com.warpgames.cambium.registry.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class ResourceSaplingBlock extends BushBlock {

    public static final MapCodec<BushBlock> CODEC = simpleCodec(properties -> new ResourceSaplingBlock(null, properties));

    private final ResourceTree tree;
    protected static final VoxelShape SHAPE = Block.box(2.0D, 0.0D, 2.0D, 14.0D, 12.0D, 14.0D);

    public ResourceSaplingBlock(ResourceTree tree, Properties properties) {
        super(properties);
        this.tree = tree;
    }

    @Override
    public MapCodec<BushBlock> codec() {
        return CODEC;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    // --- PLACEMENT LOGIC ---
    @Override
    protected boolean mayPlaceOn(BlockState state, BlockGetter level, BlockPos pos) {
        return state.is(ModBlocks.MINERAL_SOIL) || super.mayPlaceOn(state, level, pos);
    }

    // --- GROWTH LOGIC ---
    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (level.getMaxLocalRawBrightness(pos.above()) >= 9 && random.nextInt(7) == 0) {
            BlockPos soilPos = pos.below();
            BlockState soilState = level.getBlockState(soilPos);
            if (soilState.is(ModBlocks.MINERAL_SOIL)) {
                BlockEntity be = level.getBlockEntity(soilPos);
                if (be instanceof MineralSoilBlockEntity soilEntity) {

                    if (soilEntity.tryConsumeCharge(50)) {
                        grow(level, pos);
                    } else {
                    }
                }
            }
        }
    }

    public void grow(ServerLevel level, BlockPos pos) {
        BlockState rootState = ModBlocks.ROOT_BLOCK.defaultBlockState();
        level.setBlock(pos, rootState, 3);
        if (level.getBlockEntity(pos) instanceof RootBlockEntity rootEntity) {
            if (this.tree != null) {
                rootEntity.setTreeType(this.tree.getName());
                rootEntity.setChanged();
            }
        }
    }
}