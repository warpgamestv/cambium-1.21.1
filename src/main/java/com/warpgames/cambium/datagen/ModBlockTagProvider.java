package com.warpgames.cambium.datagen;

import com.warpgames.cambium.content.ResourceTree;
import com.warpgames.cambium.registry.ModBlocks;
import com.warpgames.cambium.registry.TreeRegistry;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.tags.BlockTags;

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
                .add(ModBlocks.ROOT_BLOCK);

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
    }
}