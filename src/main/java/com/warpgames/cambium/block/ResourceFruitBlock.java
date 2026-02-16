package com.warpgames.cambium.block;

import com.warpgames.cambium.content.ResourceTree;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items; // Placeholder for Raw Iron
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class ResourceFruitBlock extends Block {

    public static final IntegerProperty AGE = IntegerProperty.create("age", 0, 2);
    private static final VoxelShape SHAPE = Block.box(4, 8, 4, 12, 16, 12);

    private final ResourceTree tree;

    public ResourceFruitBlock(ResourceTree tree, Properties properties) {
        super(properties);
        this.tree = tree; // Save it
        this.registerDefaultState(this.stateDefinition.any().setValue(AGE, 0));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(AGE);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        // I can only survive if the block ABOVE me is Living Leaves
        BlockPos blockAbove = pos.above();
        return level.getBlockState(blockAbove).getBlock() instanceof LivingLeavesBlock;
    }

    // 2. THE ENFORCER (Use 'updateShape' for Mojang Mappings)
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos currentPos, BlockPos neighborPos) {
        // If the update is from ABOVE and I can't survive...
        if (direction == Direction.UP && !this.canSurvive(state, (LevelReader) level, currentPos)) {
            return Blocks.AIR.defaultBlockState();
        }

        return state;
    }

    // --- THE GRAVITY LOGIC ---
    // This method will be called by the Tree when it decides the fruit is ready
    public void dropFruit(ServerLevel level, BlockPos pos) {
        // 1. Remove the block
        level.removeBlock(pos, false);

        // 2. Spawn the Item DYNAMICALLY
        Item itemToDrop = this.tree.getItem(); // This triggers the Supplier lookup

        // SAFETY CHECK: Only drop if the item is not Air (i.e., the mod is installed)
        if (itemToDrop != null && itemToDrop != Items.AIR) {
            popResource(level, pos, new ItemStack(itemToDrop));
        }

        // 3. Play a sound
        level.levelEvent(2001, pos, Block.getId(this.defaultBlockState()));
    }
}