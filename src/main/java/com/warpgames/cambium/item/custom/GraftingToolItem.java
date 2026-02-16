package com.warpgames.cambium.item.custom;

import com.warpgames.cambium.block.transport.DuctConnection;
import com.warpgames.cambium.block.transport.PhloemDuctBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.EnumProperty;

public class GraftingToolItem extends Item {
    public GraftingToolItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos clickedPos = context.getClickedPos();
        BlockState clickedState = level.getBlockState(clickedPos);
        Direction clickedFace = context.getClickedFace();

        // Logic runs on Server Side only
        if (level.isClientSide()) return InteractionResult.SUCCESS;

        // CASE 1: The player clicked the Duct directly
        if (clickedState.getBlock() instanceof PhloemDuctBlock) {
            return toggleConnection(level, clickedPos, clickedState, clickedFace);
        }

        // CASE 2: The player clicked a Chest/Machine (Targeting the Duct behind it)
        // We look at the block adjacent to the clicked face
        BlockPos neighborPos = clickedPos.relative(clickedFace);
        BlockState neighborState = level.getBlockState(neighborPos);

        if (neighborState.getBlock() instanceof PhloemDuctBlock) {
            // We clicked the "North" face of a chest. This means the Duct is to the North.
            // That Duct connects to the chest via its "South" face.
            Direction ductFaceToToggle = clickedFace.getOpposite();
            return toggleConnection(level, neighborPos, neighborState, ductFaceToToggle);
        }

        return InteractionResult.PASS;
    }

    private InteractionResult toggleConnection(Level level, BlockPos pos, BlockState state, Direction face) {
        EnumProperty<DuctConnection> property = getPropertyForFace(face);
        DuctConnection current = state.getValue(property);

        // Cycle: NONE -> NORMAL -> EXTRACT -> NONE
        DuctConnection next = switch (current) {
            case NONE -> DuctConnection.NORMAL;
            case NORMAL -> DuctConnection.EXTRACT;
            case EXTRACT -> DuctConnection.NONE;
        };

        level.setBlock(pos, state.setValue(property, next), 3);
        return InteractionResult.CONSUME;
    }

    private EnumProperty<DuctConnection> getPropertyForFace(Direction face) {
        return switch (face) {
            case NORTH -> PhloemDuctBlock.NORTH;
            case SOUTH -> PhloemDuctBlock.SOUTH;
            case EAST -> PhloemDuctBlock.EAST;
            case WEST -> PhloemDuctBlock.WEST;
            case UP -> PhloemDuctBlock.UP;
            case DOWN -> PhloemDuctBlock.DOWN;
        };
    }
}