package com.warpgames.cambium.block.entity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.warpgames.cambium.block.ResourceFruitBlock;
import com.warpgames.cambium.content.ResourceTree;
import com.warpgames.cambium.registry.ModBlockEntities;
import com.warpgames.cambium.registry.ModBlocks;
import com.warpgames.cambium.registry.TreeRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup; // Import 1
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;   // Import 2
import net.minecraft.nbt.NbtOps;        // Import 3: Essential for Codecs in 1.21.1
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;

public class RootBlockEntity extends BlockEntity {

    private int timer = 0;
    private int growthIndex = 0;
    private String treeType = "";
    private List<BuildStep> buildPlan = new ArrayList<>();
    private boolean isPlanGenerated = false;

    public RootBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.ROOT_BE, pos, state);
    }

    // --- 1.21.1 SAVE/LOAD LOGIC ---

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.saveAdditional(tag, provider);
        tag.putString("TreeType", this.treeType);
        tag.putInt("GrowthIndex", this.growthIndex);
        tag.putBoolean("IsPlanGenerated", this.isPlanGenerated);
        tag.putInt("Timer", this.timer);

        // Save Codec List using NbtOps
        BuildStep.CODEC.listOf().encodeStart(NbtOps.INSTANCE, this.buildPlan)
                .resultOrPartial(e -> {})
                .ifPresent(nbt -> tag.put("BuildPlan", nbt));
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.loadAdditional(tag, provider);
        this.treeType = tag.getString("TreeType");
        this.growthIndex = tag.getInt("GrowthIndex");
        this.isPlanGenerated = tag.getBoolean("IsPlanGenerated");
        this.timer = tag.getInt("Timer");

        // Load Codec List using NbtOps
        if (tag.contains("BuildPlan")) {
            BuildStep.CODEC.listOf().parse(NbtOps.INSTANCE, tag.get("BuildPlan"))
                    .resultOrPartial(e -> {})
                    .ifPresent(list -> this.buildPlan = list);
        }
    }

    // --- BUILD STEP RECORD ---
    public record BuildStep(Vec3i offset, BlockState state) {
        public static final Codec<BuildStep> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Vec3i.CODEC.fieldOf("offset").forGetter(BuildStep::offset),
                BlockState.CODEC.fieldOf("state").forGetter(BuildStep::state)
        ).apply(instance, BuildStep::new));
    }

    // [KEEP STANDARD LOGIC BELOW UNCHANGED]
    public void setTreeType(String name) { this.treeType = name; setChanged(); }

    // ... [Paste the rest of your logic methods here: getTree, generateTree, tick, etc.]
    private ResourceTree getTree() {
        return TreeRegistry.TREES.stream()
                .filter(t -> t.getName().equals(this.treeType))
                .findFirst()
                .orElse(TreeRegistry.IRON);
    }

    private boolean hasValidSoil(Level level, BlockPos pos) {
        BlockPos soilPos = pos.below();
        BlockState soilState = level.getBlockState(soilPos);
        return soilState.is(ModBlocks.MINERAL_SOIL);
    }

    private void generateTree() {
        long seed = this.worldPosition.asLong();
        RandomSource random = RandomSource.create(seed);

        ResourceTree treeDef = getTree();
        BlockState logState = ModBlocks.LIVING_LOG.defaultBlockState();
        BlockState leafState = treeDef.getLeaves().defaultBlockState();
        BlockState fruitState = treeDef.getFruit().defaultBlockState().setValue(ResourceFruitBlock.AGE, 0);

        boolean isRunt = random.nextInt(10) == 0;
        int height = isRunt ? (random.nextInt(2) + 4) : (random.nextInt(4) + 6);

        for (int y = 1; y <= height; y++) {
            addStep(new Vec3i(0, y, 0), logState);

            if (y > 2 && y < height && (y % 2 != 0)) {
                if (isRunt) {
                    if (random.nextInt(10) < 4) {
                        Direction dir = Direction.Plane.HORIZONTAL.getRandomDirection(random);
                        generate3DBranch(y, dir, random, logState, leafState, fruitState);
                    }
                } else {
                    for (Direction dir : Direction.Plane.HORIZONTAL) {
                        if (random.nextInt(10) < 6) {
                            generate3DBranch(y, dir, random, logState, leafState, fruitState);
                        }
                    }
                }
            }
        }

        generateLeafCluster(new Vec3i(0, height + 1, 0), leafState);
        if (!isRunt) {
            addStep(new Vec3i(0, height + 2, 0), leafState);
        }
    }

    private void generate3DBranch(int y, Direction dir, RandomSource random, BlockState logState, BlockState leafState, BlockState fruitState) {
        Vec3i dirVec = dir.getNormal();
        Vec3i branchPos = new Vec3i(dirVec.getX(), y, dirVec.getZ());
        Direction.Axis axis = dir.getAxis();
        BlockState rotatedLog = logState.setValue(net.minecraft.world.level.block.RotatedPillarBlock.AXIS, axis);
        addStep(branchPos, rotatedLog);

        Vec3i tipPos = new Vec3i(dirVec.getX() * 2, y, dirVec.getZ() * 2);
        generateLeafCluster(tipPos, leafState);

        if (random.nextBoolean()) {
            Vec3i fruitPos = new Vec3i(tipPos.getX(), tipPos.getY() - 1, tipPos.getZ());
            addStep(fruitPos, fruitState);
        }
    }

    private void generateLeafCluster(Vec3i center, BlockState leafState) {
        addStep(center, leafState);
        addStep(new Vec3i(center.getX() + 1, center.getY(), center.getZ()), leafState);
        addStep(new Vec3i(center.getX() - 1, center.getY(), center.getZ()), leafState);
        addStep(new Vec3i(center.getX(), center.getY(), center.getZ() + 1), leafState);
        addStep(new Vec3i(center.getX(), center.getY(), center.getZ() - 1), leafState);
    }

    private void addStep(Vec3i offset, BlockState state) {
        buildPlan.add(new BuildStep(offset, state));
    }

    public static void tick(Level level, BlockPos pos, BlockState state, RootBlockEntity entity) {
        if (level.isClientSide()) return;

        if (!entity.isPlanGenerated) {
            if (entity.hasValidSoil(level, pos)) {
                entity.generateTree();
                entity.isPlanGenerated = true;
                entity.setChanged();
            }
            return;
        }

        // Regrowth Logic
        if (entity.growthIndex >= entity.buildPlan.size()) {
            entity.timer++;
            if (entity.timer >= 20) {
                ResourceTree treeDef = entity.getTree();

                // Because we fixed Loading, this loop will now work after a reload!
                for (BuildStep step : entity.buildPlan) {
                    if (step.state().getBlock() == treeDef.getFruit()) {
                        BlockPos fruitPos = pos.offset(step.offset());
                        BlockState currentBlock = level.getBlockState(fruitPos);

                        if (level.random.nextInt(10) == 0) {
                            if (level.isEmptyBlock(fruitPos)) {
                                if (entity.tryUseSoilCharge(level, pos)) {
                                    level.setBlock(fruitPos, treeDef.getFruit().defaultBlockState().setValue(ResourceFruitBlock.AGE, 0), 3);
                                    level.levelEvent(2005, fruitPos, 0);
                                }
                            } else if (currentBlock.getBlock() == treeDef.getFruit()) {
                                int age = currentBlock.getValue(ResourceFruitBlock.AGE);
                                if (age < 2) {
                                    if (entity.tryUseSoilCharge(level, pos)) {
                                        level.setBlock(fruitPos, currentBlock.setValue(ResourceFruitBlock.AGE, age + 1), 3);
                                    }
                                } else {
                                    if (level instanceof ServerLevel serverLevel && currentBlock.getBlock() instanceof ResourceFruitBlock rfb) {
                                        rfb.dropFruit(serverLevel, fruitPos);
                                    }
                                }
                            }
                        }
                    }
                }
                entity.timer = 0;
            }
            return;
        }

        // Growth Animation
        entity.timer++;
        if (entity.timer >= 5) {
            if (entity.growthIndex < entity.buildPlan.size()) {
                BuildStep step = entity.buildPlan.get(entity.growthIndex);
                BlockPos targetPos = pos.offset(step.offset());
                if (level.isEmptyBlock(targetPos)) {
                    level.setBlock(targetPos, step.state(), 3);
                    level.levelEvent(2001, targetPos, net.minecraft.world.level.block.Block.getId(step.state()));
                    if (step.state().getBlock() instanceof LeavesBlock) {
                        level.scheduleTick(targetPos, step.state().getBlock(), 1);
                    }
                }
                entity.growthIndex++;
                entity.timer = 0;
                entity.setChanged();
            }
        }
    }

    private boolean tryUseSoilCharge(Level level, BlockPos rootPos) {
        BlockPos soilPos = rootPos.below();
        if (level.getBlockEntity(soilPos) instanceof MineralSoilBlockEntity soil) {
            return soil.tryConsumeCharge(2);
        }
        return false;
    }
}