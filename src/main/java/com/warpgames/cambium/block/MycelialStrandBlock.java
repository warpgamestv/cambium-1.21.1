package com.warpgames.cambium.block;

import com.warpgames.cambium.registry.ModTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.EnumMap;
import java.util.Map;

public class MycelialStrandBlock extends Block {

    public static final EnumProperty<StrandConnection> NORTH = EnumProperty.create("north", StrandConnection.class);
    public static final EnumProperty<StrandConnection> EAST = EnumProperty.create("east", StrandConnection.class);
    public static final EnumProperty<StrandConnection> SOUTH = EnumProperty.create("south", StrandConnection.class);
    public static final EnumProperty<StrandConnection> WEST = EnumProperty.create("west", StrandConnection.class);
    public static final EnumProperty<StrandConnection> UP = EnumProperty.create("up", StrandConnection.class);
    public static final EnumProperty<StrandConnection> DOWN = EnumProperty.create("down", StrandConnection.class);

    private static final Map<Direction, EnumProperty<StrandConnection>> PROPERTY_BY_DIRECTION = new EnumMap<>(
            Direction.class);
    static {
        PROPERTY_BY_DIRECTION.put(Direction.NORTH, NORTH);
        PROPERTY_BY_DIRECTION.put(Direction.EAST, EAST);
        PROPERTY_BY_DIRECTION.put(Direction.SOUTH, SOUTH);
        PROPERTY_BY_DIRECTION.put(Direction.WEST, WEST);
        PROPERTY_BY_DIRECTION.put(Direction.UP, UP);
        PROPERTY_BY_DIRECTION.put(Direction.DOWN, DOWN);
    }

    private static final VoxelShape CORE = Block.box(5, 5, 5, 11, 11, 11);
    private static final Map<Direction, VoxelShape> ARM_SHAPES = new EnumMap<>(Direction.class);
    static {
        ARM_SHAPES.put(Direction.NORTH, Block.box(5, 5, 0, 11, 11, 5));
        ARM_SHAPES.put(Direction.SOUTH, Block.box(5, 5, 11, 11, 11, 16));
        ARM_SHAPES.put(Direction.EAST, Block.box(11, 5, 5, 16, 11, 11));
        ARM_SHAPES.put(Direction.WEST, Block.box(0, 5, 5, 5, 11, 11));
        ARM_SHAPES.put(Direction.UP, Block.box(5, 11, 5, 11, 16, 11));
        ARM_SHAPES.put(Direction.DOWN, Block.box(5, 0, 5, 11, 5, 11));
    }

    public MycelialStrandBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(NORTH, StrandConnection.NONE)
                .setValue(EAST, StrandConnection.NONE)
                .setValue(SOUTH, StrandConnection.NONE)
                .setValue(WEST, StrandConnection.NONE)
                .setValue(UP, StrandConnection.NONE)
                .setValue(DOWN, StrandConnection.NONE));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(NORTH, EAST, SOUTH, WEST, UP, DOWN);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState state = this.defaultBlockState();

        for (Direction dir : Direction.values()) {
            BlockPos neighborPos = pos.relative(dir);
            BlockState neighborState = level.getBlockState(neighborPos);
            boolean connects = canConnectTo(neighborState);
            state = state.setValue(PROPERTY_BY_DIRECTION.get(dir),
                    connects ? StrandConnection.CONNECTED : StrandConnection.NONE);
        }
        return state;
    }

    // 1.21.1 updateShape signature uses LevelAccessor
    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState,
            LevelAccessor level, BlockPos currentPos, BlockPos neighborPos) {

        EnumProperty<StrandConnection> prop = PROPERTY_BY_DIRECTION.get(direction);
        StrandConnection current = state.getValue(prop);

        if (current == StrandConnection.LOCKED) {
            return state;
        }

        boolean connects = canConnectTo(neighborState);
        return state.setValue(prop, connects ? StrandConnection.CONNECTED : StrandConnection.NONE);
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
        if (!oldState.is(state.getBlock())) {
            level.updateNeighborsAt(pos, this);
        }
    }

    // 1.21.1 uses onRemove, not affectNeighborsAfterRemoval
    @SuppressWarnings("deprecation")
    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!state.is(newState.getBlock())) {
            level.updateNeighborsAt(pos, this);
        }
        super.onRemove(state, level, pos, newState, isMoving);
    }

    public boolean canConnectTo(BlockState state) {
        return state.getBlock() instanceof MycelialStrandBlock
                || state.getBlock() instanceof MycelialNodeBlock
                || state.is(ModTags.Blocks.MYCELIAL_CONNECTABLE_STORAGE);
    }

    public static EnumProperty<StrandConnection> getProperty(Direction dir) {
        return PROPERTY_BY_DIRECTION.get(dir);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        VoxelShape shape = CORE;
        for (Direction dir : Direction.values()) {
            if (state.getValue(PROPERTY_BY_DIRECTION.get(dir)) == StrandConnection.CONNECTED) {
                shape = Shapes.or(shape, ARM_SHAPES.get(dir));
            }
        }
        return shape;
    }
}
