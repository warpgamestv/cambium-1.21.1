package com.warpgames.cambium.item.custom;

import com.warpgames.cambium.block.MycelialStrandBlock;
import com.warpgames.cambium.block.StrandConnection;
import com.warpgames.cambium.block.transport.DuctConnection;
import com.warpgames.cambium.block.transport.PhloemDuctBlock;
import com.warpgames.cambium.content.ResourceTree;
import com.warpgames.cambium.registry.TreeRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
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
        Player player = context.getPlayer();

        if (player == null)
            return InteractionResult.PASS;

        // --- MODE 1: GRAFTING (Targeting Saplings) ---
        if (clickedState.is(BlockTags.SAPLINGS)) {
            ItemStack offhandStack = player.getItemBySlot(EquipmentSlot.OFFHAND);

            if (!offhandStack.isEmpty()) {
                for (ResourceTree tree : TreeRegistry.TREES) {

                    boolean isMatch = (tree.getIngredientTag() != null && offhandStack.is(tree.getIngredientTag()))
                            || offhandStack.is(tree.getItem());

                    if (isMatch) {
                        if (!level.isClientSide()) {

                            if (!player.isCreative()) {
                                offhandStack.shrink(1);
                                context.getItemInHand().hurtAndBreak(1, player, EquipmentSlot.MAINHAND);
                            }

                            if (level.random.nextFloat() < 0.75f) {
                                level.setBlock(clickedPos, tree.getSapling().defaultBlockState(), Block.UPDATE_ALL);

                                level.playSound(null, clickedPos, SoundEvents.BONE_MEAL_USE, SoundSource.BLOCKS, 1.0f,
                                        1.0f);
                                ((ServerLevel) level).sendParticles(ParticleTypes.HAPPY_VILLAGER,
                                        clickedPos.getX() + 0.5, clickedPos.getY() + 0.5, clickedPos.getZ() + 0.5,
                                        10, 0.2, 0.2, 0.2, 0.05);
                            } else {
                                level.setBlock(clickedPos, Blocks.DEAD_BUSH.defaultBlockState(), Block.UPDATE_ALL);

                                level.playSound(null, clickedPos, SoundEvents.CROP_BREAK, SoundSource.BLOCKS, 1.0f,
                                        0.8f);
                                ((ServerLevel) level).sendParticles(ParticleTypes.SMOKE,
                                        clickedPos.getX() + 0.5, clickedPos.getY() + 0.5, clickedPos.getZ() + 0.5,
                                        10, 0.1, 0.1, 0.1, 0.05);
                            }
                        }
                        return InteractionResult.SUCCESS;
                    }
                }
            }
        }

        // --- MODE 2 & 3: WRENCH (Targeting Ducts or Strands) ---

        // Skip Client Side for wrenching to prevent desync
        if (level.isClientSide())
            return InteractionResult.SUCCESS;

        // Case A: Directly clicked a Duct
        if (clickedState.getBlock() instanceof PhloemDuctBlock) {
            return toggleDuctConnection(level, clickedPos, clickedState, clickedFace);
        }

        // Case B: Directly clicked a Strand
        if (clickedState.getBlock() instanceof MycelialStrandBlock) {
            return toggleStrandConnection(level, clickedPos, clickedState, clickedFace);
        }

        // Case C: Clicked a Machine/Container (Toggle the duct or strand behind it)
        BlockPos neighborPos = clickedPos.relative(clickedFace);
        BlockState neighborState = level.getBlockState(neighborPos);

        if (neighborState.getBlock() instanceof PhloemDuctBlock) {
            return toggleDuctConnection(level, neighborPos, neighborState, clickedFace.getOpposite());
        }

        if (neighborState.getBlock() instanceof MycelialStrandBlock) {
            return toggleStrandConnection(level, neighborPos, neighborState, clickedFace.getOpposite());
        }

        return InteractionResult.PASS;
    }

    // --- Helper for Duct Wrench Logic ---
    private InteractionResult toggleDuctConnection(Level level, BlockPos pos, BlockState state, Direction face) {
        EnumProperty<DuctConnection> property = getDuctPropertyForFace(face);
        DuctConnection current = state.getValue(property);

        DuctConnection next = switch (current) {
            case NONE -> DuctConnection.NORMAL;
            case NORMAL -> DuctConnection.EXTRACT;
            case EXTRACT -> DuctConnection.NONE;
        };

        level.setBlock(pos, state.setValue(property, next), 3);
        level.playSound(null, pos, SoundEvents.COPPER_PLACE, SoundSource.BLOCKS, 0.5f, 1.5f);
        return InteractionResult.CONSUME;
    }

    // --- Helper for Strand Wrench Logic ---
    private InteractionResult toggleStrandConnection(Level level, BlockPos pos, BlockState state, Direction face) {
        EnumProperty<StrandConnection> property = MycelialStrandBlock.getProperty(face);
        StrandConnection current = state.getValue(property);

        StrandConnection next = switch (current) {
            case CONNECTED -> StrandConnection.LOCKED;
            case LOCKED -> StrandConnection.CONNECTED;
            case NONE -> StrandConnection.NONE;
        };

        if (next == current) {
            return InteractionResult.PASS;
        }

        level.setBlock(pos, state.setValue(property, next), 3);
        level.playSound(null, pos, SoundEvents.COPPER_PLACE, SoundSource.BLOCKS, 0.5f, 1.5f);
        return InteractionResult.CONSUME;
    }

    private EnumProperty<DuctConnection> getDuctPropertyForFace(Direction face) {
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