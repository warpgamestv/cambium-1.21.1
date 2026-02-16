package com.warpgames.cambium.compat.jei;

import com.warpgames.cambium.Cambium;
import com.warpgames.cambium.recipe.SolarConcentratorRecipe;
import com.warpgames.cambium.registry.ModBlocks;
import com.warpgames.cambium.registry.ModItems;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.gui.widgets.IRecipeExtrasBuilder;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType; // Changed from IRecipeType
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class SolarConcentratorCategory implements IRecipeCategory<SolarConcentratorRecipe> {

    public static final ResourceLocation UID = ResourceLocation.fromNamespaceAndPath(Cambium.MOD_ID, "solar_concentrator");
    public static final RecipeType<SolarConcentratorRecipe> RECIPE_TYPE =
            RecipeType.create(Cambium.MOD_ID, "solar_concentrator", SolarConcentratorRecipe.class);

    private final IDrawable background;
    private final IDrawable icon;
    private final IDrawableStatic slotDrawable;

    public SolarConcentratorCategory(IGuiHelper helper) {
        ResourceLocation texture = ResourceLocation.fromNamespaceAndPath(Cambium.MOD_ID, "textures/gui/solar_concentrator.png");
        this.background = helper.createDrawable(texture, 38, 10, 106, 64);
        this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModBlocks.SOLAR_CONCENTRATOR));
        this.slotDrawable = helper.getSlotDrawable();
    }

    @Override
    public RecipeType<SolarConcentratorRecipe> getRecipeType() {
        return RECIPE_TYPE;
    }

    @Override
    public int getWidth() { return this.background.getWidth(); }

    @Override
    public int getHeight() { return this.background.getHeight(); }

    @Override
    public Component getTitle() { return Component.literal("Solar Concentrator"); }

    @Override
    public IDrawable getIcon() { return this.icon; }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, SolarConcentratorRecipe recipe, IFocusGroup focuses) {

        // 1. LENS SLOT
        var lensSlot = builder.addSlot(RecipeIngredientRole.INPUT, 44, 8)
                .setBackground(slotDrawable, -1, -1);

        if (recipe.requiresLens()) {
            lensSlot.addIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModItems.SOLAR_LENS));
            lensSlot.addRichTooltipCallback((view, list) -> {
                list.add(Component.literal("Required").withStyle(ChatFormatting.RED));
            });
        } else {
            // Optional to show it as an upgrade if not required
            lensSlot.addIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModItems.SOLAR_LENS));
            lensSlot.addRichTooltipCallback((view, list) -> {
                list.add(Component.literal("Optional Upgrade (2x Speed)").withStyle(ChatFormatting.GOLD));
            });
        }

        // 2. INPUT SLOT
        builder.addSlot(RecipeIngredientRole.INPUT, 6, 26)
                .setBackground(slotDrawable, -1, -1)
                .addIngredients(recipe.getInput());

        // 3. OUTPUT SLOT
        builder.addSlot(RecipeIngredientRole.OUTPUT, 78, 26)
                .setBackground(slotDrawable, -1, -1)
                .addItemStack(recipe.getOutput());
    }

    @Override
    public void createRecipeExtras(IRecipeExtrasBuilder builder, SolarConcentratorRecipe recipe, IFocusGroup focuses) {
        builder.addAnimatedRecipeArrow(recipe.getCookingTime())
                .setPosition(38, 26);
    }

    @Override
    public void draw(SolarConcentratorRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        Minecraft minecraft = Minecraft.getInstance();
        Font font = minecraft.font;

        float xp = recipe.getExperience();
        if (xp > 0) {
            String xpString = String.format("%.1f XP", xp);
            int stringWidth = font.width(xpString);
            guiGraphics.drawString(font, xpString, 87 - (stringWidth / 2), 5, 0x808080, false);
        }

        int cookTime = recipe.getCookingTime();
        if (cookTime > 0) {
            int seconds = cookTime / 20;
            String timeString = seconds + "s";
            int stringWidth = font.width(timeString);
            guiGraphics.drawString(font, timeString, 87 - (stringWidth / 2), 56, 0x808080, false);
        }
    }
}