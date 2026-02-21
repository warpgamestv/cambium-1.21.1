package com.warpgames.cambium.datagen;

import com.warpgames.cambium.content.ResourceTree;
import com.warpgames.cambium.registry.ModBlocks;
import com.warpgames.cambium.registry.ModTags;
import com.warpgames.cambium.registry.TreeRegistry;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Blocks;

import java.util.concurrent.CompletableFuture;

public class ModBlockTagProvider extends FabricTagProvider.BlockTagProvider {

    public ModBlockTagProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void addTags(HolderLookup.Provider wrapperLookup) {

        // Logs
        getOrCreateTagBuilder(BlockTags.LOGS)
                .add(ModBlocks.LIVING_LOG)
                .add(ModBlocks.ROOT_BLOCK);

        getOrCreateTagBuilder(BlockTags.MINEABLE_WITH_AXE)
                .add(ModBlocks.LIVING_LOG)
                .add(ModBlocks.ROOT_BLOCK)
                .add(ModBlocks.MYCELIAL_NODE)
                .add(ModBlocks.MYCELIAL_STRAND)
                .add(ModBlocks.PHLOEM_DUCT);

        // Soil
        getOrCreateTagBuilder(BlockTags.MINEABLE_WITH_SHOVEL)
                .add(ModBlocks.MINERAL_SOIL);

        // Leaves & Hoes
        var leavesTagBuilder = getOrCreateTagBuilder(BlockTags.LEAVES);
        var hoeTagBuilder = getOrCreateTagBuilder(BlockTags.MINEABLE_WITH_HOE);

        for (ResourceTree tree : TreeRegistry.TREES) {
            if (tree.getLeaves() != null) {
                leavesTagBuilder.add(tree.getLeaves());
                hoeTagBuilder.add(tree.getLeaves());
            }
        }

        // Mycelial Connectable Storage
        getOrCreateTagBuilder(ModTags.Blocks.MYCELIAL_CONNECTABLE_STORAGE)
                .add(Blocks.CHEST)
                .add(Blocks.TRAPPED_CHEST)
                .add(Blocks.BARREL)
                .add(Blocks.HOPPER)
                .add(Blocks.SHULKER_BOX)
                .add(Blocks.WHITE_SHULKER_BOX)
                .add(Blocks.ORANGE_SHULKER_BOX)
                .add(Blocks.MAGENTA_SHULKER_BOX)
                .add(Blocks.LIGHT_BLUE_SHULKER_BOX)
                .add(Blocks.YELLOW_SHULKER_BOX)
                .add(Blocks.LIME_SHULKER_BOX)
                .add(Blocks.PINK_SHULKER_BOX)
                .add(Blocks.GRAY_SHULKER_BOX)
                .add(Blocks.LIGHT_GRAY_SHULKER_BOX)
                .add(Blocks.CYAN_SHULKER_BOX)
                .add(Blocks.PURPLE_SHULKER_BOX)
                .add(Blocks.BLUE_SHULKER_BOX)
                .add(Blocks.BROWN_SHULKER_BOX)
                .add(Blocks.GREEN_SHULKER_BOX)
                .add(Blocks.RED_SHULKER_BOX)
                .add(Blocks.BLACK_SHULKER_BOX);
    }
}