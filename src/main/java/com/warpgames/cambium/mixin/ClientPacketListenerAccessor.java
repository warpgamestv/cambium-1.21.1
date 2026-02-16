package com.warpgames.cambium.mixin;

import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.world.item.crafting.RecipeManager; // Fix Import
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ClientPacketListener.class)
public interface ClientPacketListenerAccessor {
    // In 1.21.1, the field is named "recipeManager" and is of type RecipeManager
    @Accessor("recipeManager")
    RecipeManager getRecipes();
}