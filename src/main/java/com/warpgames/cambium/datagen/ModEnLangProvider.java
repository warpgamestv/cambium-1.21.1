package com.warpgames.cambium.datagen;

import com.warpgames.cambium.content.ResourceTree;
import com.warpgames.cambium.registry.ModBlocks;
import com.warpgames.cambium.registry.ModItems;
import com.warpgames.cambium.registry.TreeRegistry;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.core.HolderLookup;

import java.util.concurrent.CompletableFuture;

public class ModEnLangProvider extends FabricLanguageProvider {

    public ModEnLangProvider(FabricDataOutput dataOutput, CompletableFuture<HolderLookup.Provider> registryLookup) {
        super(dataOutput, registryLookup);
    }

    @Override
    public void generateTranslations(HolderLookup.Provider registryLookup, TranslationBuilder builder) {
        // --- Item Groups ---
        builder.add("itemGroup.cambium", "Cambium");
        builder.add("itemGroup.cambium_trees", "Cambium Resource Trees");

        // --- Blocks ---
        // Registering the block automatically handles the Item name for BlockItems
        builder.add(ModBlocks.SOLAR_DIGESTER, "Solar Digester");
        builder.add(ModBlocks.SOLAR_CONCENTRATOR, "Solar Concentrator");
        builder.add(ModBlocks.MINERAL_SOIL, "Mineral Soil");
        builder.add(ModBlocks.LIVING_LOG, "Living Log");
        builder.add(ModBlocks.ROOT_BLOCK, "Root Block");
        builder.add(ModBlocks.GRAVITROPIC_NODE, "Gravitropic Node");
        builder.add(ModBlocks.PHLOEM_DUCT, "Phloem Duct");

        // --- Items ---
        builder.add(ModItems.SOLAR_LENS, "Solar Lens");
        builder.add(ModItems.ORGANIC_ASH, "Organic Ash");
        builder.add(ModItems.BIOCOMPOSITE_PASTE, "Biocomposite Paste");
        builder.add(ModItems.BIOPOLYMER, "Biopolymer");
        builder.add(ModItems.BIOPOLYMER_CASING, "Biopolymer Casing");
        builder.add(ModItems.GRAFTING_TOOL, "Grafting Tool");

        // --- Dynamic Resource Tree Support ---
        for (ResourceTree tree : TreeRegistry.TREES) {
            String rawName = tree.getName();
            String capitalizedName = rawName.substring(0, 1).toUpperCase() + rawName.substring(1).toLowerCase();

            // Registering the Block handles both Block and Item translations
            builder.add(tree.getLeaves(), capitalizedName + " Leaves");
            builder.add(tree.getFruit(), capitalizedName + " Fruit");
            builder.add(tree.getSapling(), capitalizedName + " Sapling");
        }

        // --- UI / Messages ---
        builder.add("container.solar_digester", "Solar Digester");
        builder.add("container.solar_concentrator", "Solar Concentrator");
        builder.add("jei.cambium.solar_digesting", "Solar Digesting");
        builder.add("jei.cambium.solar_concentrating", "Solar Concentrating");
    }}