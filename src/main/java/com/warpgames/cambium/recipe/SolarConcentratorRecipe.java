package com.warpgames.cambium.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.warpgames.cambium.registry.ModBlocks;
import com.warpgames.cambium.recipe.ModRecipes;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

public class SolarConcentratorRecipe implements Recipe<SingleRecipeInput> {

    private final Ingredient input;
    private final ItemStack output;
    private final int cookingTime;
    private final float experience;
    private final boolean requiresLens;
    private final String group;

    public SolarConcentratorRecipe(Ingredient input, ItemStack output, int cookingTime, float experience, boolean requiresLens) {
        this.input = input;
        this.output = output;
        this.cookingTime = cookingTime;
        this.experience = experience;
        this.requiresLens = requiresLens;
        this.group = "";
    }

    // --- GETTERS ---
    public Ingredient getInput() { return input; }
    public ItemStack getOutput() { return output.copy(); }
    public int getCookingTime() { return cookingTime; }
    public float getExperience() { return experience; }
    public boolean requiresLens() { return requiresLens; }

    // --- STANDARD LOGIC (1.21.1) ---

    public boolean matches(Container inv, Level level) {
        // Assumes slot 0 is the input slot in the machine container
        return this.input.test(inv.getItem(0));
    }

    public ItemStack assemble(Container inv, HolderLookup.Provider lookup) {
        return output.copy();
    }

    @Override
    public boolean matches(SingleRecipeInput recipeInput, Level level) {
        return false;
    }

    @Override
    public ItemStack assemble(SingleRecipeInput recipeInput, HolderLookup.Provider provider) {
        return null;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider lookup) {
        return output.copy();
    }

    public String group() {
        return this.group;
    }

    @Override
    public ItemStack getToastSymbol() {
        return new ItemStack(ModBlocks.SOLAR_CONCENTRATOR);
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.SOLAR_CONCENTRATOR_SERIALIZER;
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipes.SOLAR_CONCENTRATOR_TYPE;
    }

    // --- THE SERIALIZER ---
    public static class Serializer implements RecipeSerializer<SolarConcentratorRecipe> {
        public static final MapCodec<SolarConcentratorRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
                Ingredient.CODEC_NONEMPTY.fieldOf("ingredient").forGetter(SolarConcentratorRecipe::getInput),
                ItemStack.CODEC.fieldOf("result").forGetter(SolarConcentratorRecipe::getOutput),
                Codec.INT.optionalFieldOf("cookingtime", 100).forGetter(SolarConcentratorRecipe::getCookingTime),
                Codec.FLOAT.optionalFieldOf("experience", 0.0f).forGetter(SolarConcentratorRecipe::getExperience),
                Codec.BOOL.optionalFieldOf("requires_lens", false).forGetter(SolarConcentratorRecipe::requiresLens)
        ).apply(inst, SolarConcentratorRecipe::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, SolarConcentratorRecipe> STREAM_CODEC = StreamCodec.composite(
                Ingredient.CONTENTS_STREAM_CODEC, SolarConcentratorRecipe::getInput,
                ItemStack.STREAM_CODEC, SolarConcentratorRecipe::getOutput,
                net.minecraft.network.codec.ByteBufCodecs.INT, SolarConcentratorRecipe::getCookingTime,
                net.minecraft.network.codec.ByteBufCodecs.FLOAT, SolarConcentratorRecipe::getExperience,
                net.minecraft.network.codec.ByteBufCodecs.BOOL, SolarConcentratorRecipe::requiresLens,
                SolarConcentratorRecipe::new
        );

        @Override
        public MapCodec<SolarConcentratorRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, SolarConcentratorRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}