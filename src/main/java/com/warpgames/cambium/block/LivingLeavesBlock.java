package com.warpgames.cambium.block;

import com.mojang.serialization.Codec; // FIXED: Added missing import
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.warpgames.cambium.block.entity.MineralSoilBlockEntity;
import com.warpgames.cambium.content.ResourceTree;
import com.warpgames.cambium.registry.TreeRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class LivingLeavesBlock extends LeavesBlock {

    public static final MapCodec<LivingLeavesBlock> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    Codec.STRING.fieldOf("tree_id").forGetter(b -> b.tree.getName()),
                    propertiesCodec()
            ).apply(instance, (name, props) -> new LivingLeavesBlock(getTreeByName(name), props))
    );

    private final ResourceTree tree;

    public LivingLeavesBlock(ResourceTree tree, Properties properties) {
        super(properties);
        this.tree = tree;
    }

    private static ResourceTree getTreeByName(String name) {
        return TreeRegistry.TREES.stream()
                .filter(t -> t.getName().equals(name))
                .findFirst()
                .orElse(TreeRegistry.IRON);
    }

    @Override
    public MapCodec<LivingLeavesBlock> codec() {
        return CODEC;
    }

    public void spawnFallingLeavesParticle(Level level, BlockPos pos, RandomSource random) {
        if (level.isClientSide() && random.nextInt(32) == 0) {
            level.addParticle(ParticleTypes.CHERRY_LEAVES,
                    pos.getX() + random.nextDouble(),
                    pos.getY() - 0.2D,
                    pos.getZ() + random.nextDouble(),
                    0.0D, 0.0D, 0.0D);
        }
    }

    @Override
    public boolean isRandomlyTicking(BlockState state) {
        return true;
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        super.randomTick(state, level, pos, random);

        if (random.nextInt(20) == 0) {
            BlockPos fruitPos = pos.below();
            if (level.isEmptyBlock(fruitPos)) {
                if (consumeSoilCharge(level, pos)) {
                    level.setBlock(fruitPos, this.tree.getFruit().defaultBlockState(), 3);
                }
            }
        }
    }

    private boolean consumeSoilCharge(ServerLevel level, BlockPos leafPos) {
        for (int i = 1; i <= 10; i++) {
            BlockPos checkPos = leafPos.below(i);
            BlockEntity be = level.getBlockEntity(checkPos);
            if (be instanceof MineralSoilBlockEntity soil) {
                return soil.tryConsumeCharge(1);
            }
        }
        return false;
    }
}