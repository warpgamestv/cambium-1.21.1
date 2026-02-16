package com.warpgames.cambium.recipe;

import com.warpgames.cambium.Cambium;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;

public class ModRecipes {

    // The Serializer
    public static final RecipeSerializer<SolarDigesterRecipe> SOLAR_DIGESTER_SERIALIZER =
            Registry.register(BuiltInRegistries.RECIPE_SERIALIZER, ResourceLocation.fromNamespaceAndPath(Cambium.MOD_ID, "solar_digesting"), new SolarDigesterRecipe.Serializer());

    public static final RecipeSerializer<SolarConcentratorRecipe> SOLAR_CONCENTRATOR_SERIALIZER =
            Registry.register(BuiltInRegistries.RECIPE_SERIALIZER, ResourceLocation.fromNamespaceAndPath(Cambium.MOD_ID, "solar_concentrating"), new SolarConcentratorRecipe.Serializer());

    // The Type
    public static final RecipeType<SolarDigesterRecipe> SOLAR_DIGESTER_TYPE =
            Registry.register(BuiltInRegistries.RECIPE_TYPE, ResourceLocation.fromNamespaceAndPath(Cambium.MOD_ID, "solar_digesting"), new RecipeType<SolarDigesterRecipe>() {
                @Override
                public String toString() {
                    return "solar_digesting";
                }
            });

    public static final RecipeType<SolarConcentratorRecipe> SOLAR_CONCENTRATOR_TYPE =
            Registry.register(BuiltInRegistries.RECIPE_TYPE, ResourceLocation.fromNamespaceAndPath(Cambium.MOD_ID, "solar_concentrating"), new RecipeType<SolarConcentratorRecipe>() {
                @Override
                public String toString() {
                    return "solar_concentrating";
                }
            });

    public static void registerRecipes() {
        Cambium.LOGGER.info("Registering Custom Recipes for " + Cambium.MOD_ID);
    }
}