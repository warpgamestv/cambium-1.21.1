package com.warpgames.cambium.datagen;

import com.warpgames.cambium.content.ResourceTree;
import com.warpgames.cambium.registry.ModBlocks;
import com.warpgames.cambium.registry.TreeRegistry;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.minecraft.core.HolderLookup;

import java.util.concurrent.CompletableFuture;

public class ModLootTableProvider extends FabricBlockLootTableProvider {

    public ModLootTableProvider(FabricDataOutput dataOutput, CompletableFuture<HolderLookup.Provider> registryLookup) {
        super(dataOutput, registryLookup);
    }

    @Override
    public void generate() {
        // 1. Static Blocks
        dropOther(ModBlocks.ROOT_BLOCK, ModBlocks.LIVING_LOG);
        dropSelf(ModBlocks.MINERAL_SOIL);
        dropSelf(ModBlocks.LIVING_LOG);
        dropSelf(ModBlocks.GRAVITROPIC_NODE);
        dropSelf(ModBlocks.MYCELIAL_NODE);
        dropSelf(ModBlocks.MYCELIAL_STRAND);
        dropSelf(ModBlocks.PHLOEM_DUCT);

        // 2. Dynamic Blocks

        add(ModBlocks.SOLAR_DIGESTER, createNameableBlockEntityTable(ModBlocks.SOLAR_DIGESTER));
        add(ModBlocks.SOLAR_CONCENTRATOR, createNameableBlockEntityTable(ModBlocks.SOLAR_CONCENTRATOR));

        // Dynamic Resource Tree Drops
        for (ResourceTree tree : TreeRegistry.TREES) {

            // LEAVES: Use "Oak-like" drops (Sticks + Rare Sapling + Silk Touch Self)
            if (tree.getLeaves() != null) {
                // Standard Vanilla Rates: 5%, 6.25%, 8.33%, 10% based on Fortune level
                add(tree.getLeaves(), block -> createLeavesDrops(
                        block,
                        tree.getSapling(),
                        0.05f, 0.0625f, 0.083333336f, 0.1f));
            }

            if (tree.getFruit() != null) {
                // Fruit drops itself
                dropSelf(tree.getFruit());
            }

            if (tree.getSapling() != null) {
                dropSelf(tree.getSapling());
            }
        }
    }
}