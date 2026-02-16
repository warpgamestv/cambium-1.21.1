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

public class SolarDigesterRecipe implements Recipe<SingleRecipeInput> {

    private final Ingredient input;
    private final ItemStack output;
    private final ItemStack byproduct;
    private final float byproductChance;
    private final int cookingTime;
    private final float experience;
    private final boolean requiresLens;
    private final String group;

    public SolarDigesterRecipe(Ingredient input, ItemStack output, ItemStack byproduct, float byproductChance, int cookingTime, float experience, boolean requiresLens) {
        this.input = input;
        this.output = output;
        this.byproduct = byproduct;
        this.byproductChance = byproductChance;
        this.cookingTime = cookingTime;
        this.experience = experience;
        this.requiresLens = requiresLens;
        this.group = "";
    }

    // --- GETTERS ---
    public Ingredient getInput() {
        return input;
    }

    public ItemStack getOutput() {
        return output.copy();
    }

    public ItemStack getByproduct() {
        return byproduct.copy();
    }

    public float getByproductChance() {
        return byproductChance;
    }

    public int getCookingTime() {
        return cookingTime;
    }

    public float getExperience() {
        return experience;
    }

    public boolean requiresLens() {
        return requiresLens;
    }

    // --- STANDARD LOGIC (1.21.1) ---

    public boolean matches(Container inv, Level level) {
        // Assumes slot 0 is input
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
        return new ItemStack(ModBlocks.SOLAR_DIGESTER);
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.SOLAR_DIGESTER_SERIALIZER;
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipes.SOLAR_DIGESTER_TYPE;
    }

    // --- THE SERIALIZER ---
    public static class Serializer implements RecipeSerializer<SolarDigesterRecipe> {
        public static final MapCodec<SolarDigesterRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
                Ingredient.CODEC_NONEMPTY.fieldOf("ingredient").forGetter(SolarDigesterRecipe::getInput),
                ItemStack.CODEC.fieldOf("result").forGetter(SolarDigesterRecipe::getOutput),
                ItemStack.CODEC.optionalFieldOf("byproduct", ItemStack.EMPTY).forGetter(SolarDigesterRecipe::getByproduct),
                Codec.FLOAT.optionalFieldOf("chance", 1.0f).forGetter(SolarDigesterRecipe::getByproductChance),
                Codec.INT.optionalFieldOf("cookingtime", 100).forGetter(SolarDigesterRecipe::getCookingTime),
                Codec.FLOAT.optionalFieldOf("experience", 0.0f).forGetter(SolarDigesterRecipe::getExperience),
                Codec.BOOL.optionalFieldOf("requires_lens", false).forGetter(SolarDigesterRecipe::requiresLens)
        ).apply(inst, SolarDigesterRecipe::new));

        // FIX: Manual StreamCodec because we have 7 arguments (Max is 6 for composite)
        public static final StreamCodec<RegistryFriendlyByteBuf, SolarDigesterRecipe> STREAM_CODEC = StreamCodec.of(
                SolarDigesterRecipe.Serializer::toNetwork,
                SolarDigesterRecipe.Serializer::fromNetwork
        );

        // 1. Write to Network
        private static void toNetwork(RegistryFriendlyByteBuf buf, SolarDigesterRecipe recipe) {
            Ingredient.CONTENTS_STREAM_CODEC.encode(buf, recipe.input);
            ItemStack.STREAM_CODEC.encode(buf, recipe.output);
            ItemStack.STREAM_CODEC.encode(buf, recipe.byproduct);
            buf.writeFloat(recipe.byproductChance);
            buf.writeInt(recipe.cookingTime);
            buf.writeFloat(recipe.experience);
            buf.writeBoolean(recipe.requiresLens);
        }

        // 2. Read from Network
        private static SolarDigesterRecipe fromNetwork(RegistryFriendlyByteBuf buf) {
            Ingredient input = Ingredient.CONTENTS_STREAM_CODEC.decode(buf);
            ItemStack output = ItemStack.STREAM_CODEC.decode(buf);
            ItemStack byproduct = ItemStack.STREAM_CODEC.decode(buf);
            float chance = buf.readFloat();
            int time = buf.readInt();
            float xp = buf.readFloat();
            boolean lens = buf.readBoolean();
            return new SolarDigesterRecipe(input, output, byproduct, chance, time, xp, lens);
        }

        @Override
        public MapCodec<SolarDigesterRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, SolarDigesterRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}