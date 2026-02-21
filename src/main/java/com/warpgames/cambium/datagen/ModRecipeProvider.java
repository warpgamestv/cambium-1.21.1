package com.warpgames.cambium.datagen;

import com.warpgames.cambium.registry.ModBlocks;
import com.warpgames.cambium.registry.ModItems;
import com.warpgames.cambium.registry.ModTags;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.*;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.concurrent.CompletableFuture;

public class ModRecipeProvider extends FabricRecipeProvider {

        public ModRecipeProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
                super(output, registriesFuture);
        }

        @Override
        public void buildRecipes(RecipeOutput output) {
                // Solar Digester Recipes
                // Note: For ingredients, we use Ingredient.of(...)
                SolarDigesterRecipeBuilder.digester(Ingredient.of(ModTags.Items.DIGESTABLE), ModItems.ORGANIC_ASH, 1)
                                .byproduct(ModItems.ORGANIC_ASH, 1, 0.25f)
                                .time(150)
                                .xp(1.0f)
                                .unlockedBy("has_logs", has(ItemTags.LOGS))
                                .save(output);

                // Solar Concentrator Recipes
                SolarConcentratorRecipeBuilder.concentrator(Items.SAND, Items.GLASS, 1)
                                .time(75)
                                .xp(0.1f)
                                .requiresLens()
                                .unlockedBy("has_sand", has(Items.SAND))
                                .save(output);

                SolarConcentratorRecipeBuilder.concentrator(ModItems.BIOCOMPOSITE_PASTE, ModItems.BIOPOLYMER, 1)
                                .time(75)
                                .xp(0.1f)
                                .requiresLens()
                                .unlockedBy("has_biocomposite_paste", has(ModItems.BIOCOMPOSITE_PASTE))
                                .save(output);

                // Crafting Table Recipes
                ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.SOLAR_DIGESTER)
                                .pattern("GGG")
                                .pattern("BCB")
                                .pattern("LLL")
                                .define('G', Items.GLASS)
                                .define('L', ItemTags.LOGS)
                                .define('C', Items.COMPOSTER)
                                .define('B', Items.COPPER_INGOT)
                                .unlockedBy("has_composter", has(Items.COMPOSTER))
                                .save(output);

                ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.SOLAR_CONCENTRATOR)
                                .pattern("GGG")
                                .pattern("CFC")
                                .pattern("PPP")
                                .define('G', Items.GLASS)
                                .define('F', Items.FURNACE)
                                .define('C', Items.COPPER_INGOT)
                                .define('P', ModItems.BIOPOLYMER_CASING)
                                .unlockedBy("has_furnace", has(Items.FURNACE))
                                .save(output);

                ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.GRAVITROPIC_NODE)
                                .pattern("BRB")
                                .pattern("LEL")
                                .pattern("BRB")
                                .define('B', ModItems.BIOPOLYMER_CASING)
                                .define('R', Items.REDSTONE)
                                .define('L', Items.LAPIS_LAZULI)
                                .define('E', Items.ENDER_PEARL)
                                .unlockedBy("has_biopolymer_casing", has(ModItems.BIOPOLYMER_CASING))
                                .save(output);

                ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.PHLOEM_DUCT)
                                .pattern("OPO")
                                .pattern("PGP")
                                .pattern("OPO")
                                .define('P', ModItems.BIOPOLYMER)
                                .define('G', Items.GLASS_PANE)
                                .define('O', ModItems.ORGANIC_ASH)
                                .unlockedBy("has_polymer", has(ModItems.BIOPOLYMER))
                                .save(output);

                ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.SOLAR_LENS)
                                .pattern(" G ")
                                .pattern("GPG")
                                .pattern(" G ")
                                .define('G', Items.GOLD_INGOT)
                                .define('P', Items.GLASS_PANE)
                                .unlockedBy("has_gold", has(Items.GOLD_INGOT))
                                .save(output);

                ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ModBlocks.MINERAL_SOIL)
                                .pattern("AAA")
                                .pattern("ADA")
                                .pattern("AAA")
                                .define('A', ModItems.ORGANIC_ASH)
                                .define('D', Items.DIRT)
                                .unlockedBy("has_ash", has(ModItems.ORGANIC_ASH))
                                .save(output);

                ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.BIOCOMPOSITE_PASTE, 2)
                                .requires(ModItems.ORGANIC_ASH)
                                .requires(Items.CLAY_BALL)
                                .unlockedBy("has_ash", has(ModItems.ORGANIC_ASH))
                                .save(output);

                SimpleCookingRecipeBuilder
                                .smelting(Ingredient.of(ModItems.BIOCOMPOSITE_PASTE), RecipeCategory.MISC,
                                                ModItems.BIOPOLYMER, 0.35F, 200)
                                .unlockedBy("has_biocomposite_paste", has(ModItems.BIOCOMPOSITE_PASTE))
                                .save(output);

                ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.BIOPOLYMER_CASING)
                                .pattern("P P")
                                .pattern("LCL")
                                .pattern("P P")
                                .define('P', ModItems.BIOPOLYMER)
                                .define('L', ModItems.SOLAR_LENS)
                                .define('C', Items.COPPER_INGOT)
                                .unlockedBy("has_polymer", has(ModItems.BIOPOLYMER))
                                .save(output);

                ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.MYCELIAL_STRAND, 6)
                                .pattern(" A ")
                                .pattern("ABA")
                                .pattern(" A ")
                                .define('A', ModItems.ORGANIC_ASH)
                                .define('B', ModItems.BIOPOLYMER_CASING)
                                .unlockedBy("has_ash", has(ModItems.ORGANIC_ASH))
                                .save(output);

                ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.MYCELIAL_NODE)
                                .pattern("SMS")
                                .pattern("SCS")
                                .pattern("SMS")
                                .define('S', ModBlocks.MYCELIAL_STRAND)
                                .define('M', ModBlocks.MINERAL_SOIL)
                                .define('C', Items.CHEST)
                                .unlockedBy("has_strand", has(ModBlocks.MYCELIAL_STRAND))
                                .save(output);

                // Armor Recipes
                ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.PHOTOVOLTAIC_HELMET)
                                .pattern("PCP")
                                .pattern("P P")
                                .define('P', ModItems.BIOPOLYMER_CASING)
                                .define('C', Items.COPPER_INGOT)
                                .unlockedBy("has_biopolymer_casing", has(ModItems.BIOPOLYMER_CASING))
                                .save(output);

                ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.PHOTOVOLTAIC_CHESTPLATE)
                                .pattern("P P")
                                .pattern("PCP")
                                .pattern("PCP")
                                .define('P', ModItems.BIOPOLYMER_CASING)
                                .define('C', Items.COPPER_INGOT)
                                .unlockedBy("has_biopolymer_casing", has(ModItems.BIOPOLYMER_CASING))
                                .save(output);

                ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.PHOTOVOLTAIC_LEGGINGS)
                                .pattern("PCP")
                                .pattern("P P")
                                .pattern("P P")
                                .define('P', ModItems.BIOPOLYMER_CASING)
                                .define('C', Items.COPPER_INGOT)
                                .unlockedBy("has_biopolymer_casing", has(ModItems.BIOPOLYMER_CASING))
                                .save(output);

                ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.PHOTOVOLTAIC_BOOTS)
                                .pattern("P P")
                                .pattern("C C")
                                .define('P', ModItems.BIOPOLYMER_CASING)
                                .define('C', Items.COPPER_INGOT)
                                .unlockedBy("has_biopolymer_casing", has(ModItems.BIOPOLYMER_CASING))
                                .save(output);
        }
}