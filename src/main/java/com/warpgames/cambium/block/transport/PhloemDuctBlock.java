package com.warpgames.cambium.block.transport;

import com.warpgames.cambium.block.entity.PhloemDuctBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.Container;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor; // Important for 1.21.1 updateShape
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;
import java.util.Map;

public class PhloemDuctBlock extends Block implements SimpleWaterloggedBlock, EntityBlock {

    public static final EnumProperty<DuctConnection> NORTH = EnumProperty.create("north", DuctConnection.class);
    public static final EnumProperty<DuctConnection> EAST = EnumProperty.create("east", DuctConnection.class);
    public static final EnumProperty<DuctConnection> SOUTH = EnumProperty.create("south", DuctConnection.class);
    public static final EnumProperty<DuctConnection> WEST = EnumProperty.create("west", DuctConnection.class);
    public static final EnumProperty<DuctConnection> UP = EnumProperty.create("up", DuctConnection.class);
    public static final EnumProperty<DuctConnection> DOWN = EnumProperty.create("down", DuctConnection.class);
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    private static final VoxelShape CORE = Block.box(5, 5, 5, 11, 11, 11);
    private static final Map<Direction, VoxelShape> SHAPES = new EnumMap<>(Direction.class);

    static {
        SHAPES.put(Direction.NORTH, Block.box(5, 5, 0, 11, 11, 5));
        SHAPES.put(Direction.SOUTH, Block.box(5, 5, 11, 11, 11, 16));
        SHAPES.put(Direction.EAST, Block.box(11, 5, 5, 16, 11, 11));
        SHAPES.put(Direction.WEST, Block.box(0, 5, 5, 5, 11, 11));
        SHAPES.put(Direction.UP, Block.box(5, 11, 5, 11, 16, 11));
        SHAPES.put(Direction.DOWN, Block.box(5, 0, 5, 11, 5, 11));
    }

    public PhloemDuctBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(NORTH, DuctConnection.NONE).setValue(EAST, DuctConnection.NONE)
                .setValue(SOUTH, DuctConnection.NONE).setValue(WEST, DuctConnection.NONE)
                .setValue(UP, DuctConnection.NONE).setValue(DOWN, DuctConnection.NONE)
                .setValue(WATERLOGGED, false));
    }

    // --- 1.21.1 UPDATE SHAPE SIGNATURE ---
    // In 1.21.1, this takes LevelAccessor, not ScheduledTickAccess/LevelReader
    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos currentPos, BlockPos neighborPos) {
        if (state.getValue(WATERLOGGED)) {
            level.scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        }

        EnumProperty<DuctConnection> prop = getProperty(direction);
        DuctConnection current = state.getValue(prop);
        boolean isValidNeighbor = canConnect(level, neighborPos, direction);

        if (!isValidNeighbor) {
            return state.setValue(prop, DuctConnection.NONE);
        } else if (current == DuctConnection.NONE) {
            return state.setValue(prop, DuctConnection.NORMAL);
        }

        return state;
    }

    // --- FORCE CONNECT ON PLACE ---
    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
        if (!oldState.is(state.getBlock())) {
            level.updateNeighborsAt(pos, this);
        }
    }

    // --- FORCE DISCONNECT ON BREAK (1.21.1) ---
    // 1.21.1 uses onRemove, not affectNeighborsAfterRemoval
    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!state.is(newState.getBlock())) {
            // Wake up neighbors so they stop trying to push items to us
            level.updateNeighborsAt(pos, this);
            super.onRemove(state, level, pos, newState, isMoving);
        }
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        FluidState fluidState = level.getFluidState(pos);

        return this.defaultBlockState()
                .setValue(WATERLOGGED, fluidState.getType() == Fluids.WATER)
                .setValue(NORTH, getInitialConnection(level, pos.north(), Direction.SOUTH))
                .setValue(SOUTH, getInitialConnection(level, pos.south(), Direction.NORTH))
                .setValue(EAST, getInitialConnection(level, pos.east(), Direction.WEST))
                .setValue(WEST, getInitialConnection(level, pos.west(), Direction.EAST))
                .setValue(UP, getInitialConnection(level, pos.above(), Direction.DOWN))
                .setValue(DOWN, getInitialConnection(level, pos.below(), Direction.UP));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(NORTH, EAST, SOUTH, WEST, UP, DOWN, WATERLOGGED);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        VoxelShape shape = CORE;
        if (state.getValue(NORTH) != DuctConnection.NONE) shape = Shapes.or(shape, SHAPES.get(Direction.NORTH));
        if (state.getValue(SOUTH) != DuctConnection.NONE) shape = Shapes.or(shape, SHAPES.get(Direction.SOUTH));
        if (state.getValue(EAST) != DuctConnection.NONE) shape = Shapes.or(shape, SHAPES.get(Direction.EAST));
        if (state.getValue(WEST) != DuctConnection.NONE) shape = Shapes.or(shape, SHAPES.get(Direction.WEST));
        if (state.getValue(UP) != DuctConnection.NONE) shape = Shapes.or(shape, SHAPES.get(Direction.UP));
        if (state.getValue(DOWN) != DuctConnection.NONE) shape = Shapes.or(shape, SHAPES.get(Direction.DOWN));
        return shape;
    }

    private DuctConnection getInitialConnection(Level level, BlockPos neighborPos, Direction dir) {
        if (canConnect(level, neighborPos, dir)) return DuctConnection.NORMAL;
        return DuctConnection.NONE;
    }

    private EnumProperty<DuctConnection> getProperty(Direction dir) {
        return switch (dir) {
            case NORTH -> NORTH;
            case SOUTH -> SOUTH;
            case EAST -> EAST;
            case WEST -> WEST;
            case UP -> UP;
            case DOWN -> DOWN;
        };
    }

    private boolean canConnect(BlockGetter level, BlockPos pos, Direction dir) {
        BlockState state = level.getBlockState(pos);
        if (state.getBlock() instanceof PhloemDuctBlock) return true;
        BlockEntity be = level.getBlockEntity(pos);
        return be instanceof Container;
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    // --- BLOCK ENTITY METHODS ---

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new PhloemDuctBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return level.isClientSide() ? null : (lvl, pos, st, be) -> {
            if (be instanceof PhloemDuctBlockEntity customBE) {
                PhloemDuctBlockEntity.serverTick(lvl, pos, st, customBE);
            }
        };
    }
}