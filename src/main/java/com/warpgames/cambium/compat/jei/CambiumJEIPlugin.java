package com.warpgames.cambium.compat.jei;

import com.warpgames.cambium.Cambium;
import com.warpgames.cambium.recipe.ModRecipes;
import com.warpgames.cambium.recipe.SolarConcentratorRecipe;
import com.warpgames.cambium.recipe.SolarDigesterRecipe;
import com.warpgames.cambium.registry.ModBlocks;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;

import java.util.List;

@JeiPlugin
public class CambiumJEIPlugin implements IModPlugin {

    @Override
    public ResourceLocation getPluginUid() {
        return ResourceLocation.fromNamespaceAndPath(Cambium.MOD_ID, "jei_plugin");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(new SolarDigesterCategory(registration.getJeiHelpers().getGuiHelper()));
        registration.addRecipeCategories(new SolarConcentratorCategory(registration.getJeiHelpers().getGuiHelper()));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        ClientLevel level = Minecraft.getInstance().level;

        if (level != null) {
            RecipeManager recipeManager = level.getRecipeManager();

            // 1. Solar Digester Recipes
            // getAllRecipesFor returns RecipeHolder<T>, we map .value() to get the raw recipe
            List<SolarDigesterRecipe> digesterRecipes = recipeManager.getAllRecipesFor(ModRecipes.SOLAR_DIGESTER_TYPE)
                    .stream()
                    .map(RecipeHolder::value)
                    .toList();

            registration.addRecipes(SolarDigesterCategory.RECIPE_TYPE, digesterRecipes);

            // 2. Solar Concentrator Recipes
            List<SolarConcentratorRecipe> concentratorRecipes = recipeManager.getAllRecipesFor(ModRecipes.SOLAR_CONCENTRATOR_TYPE)
                    .stream()
                    .map(RecipeHolder::value)
                    .toList();

            registration.addRecipes(SolarConcentratorCategory.RECIPE_TYPE, concentratorRecipes);
        }
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(VanillaTypes.ITEM_STACK, new ItemStack(ModBlocks.SOLAR_DIGESTER), SolarDigesterCategory.RECIPE_TYPE);
        registration.addRecipeCatalyst(VanillaTypes.ITEM_STACK, new ItemStack(ModBlocks.SOLAR_CONCENTRATOR), SolarConcentratorCategory.RECIPE_TYPE);
    }
}