package com.warpgames.cambium.registry;

import com.warpgames.cambium.Cambium;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class ModTags {
    public static class Items {
        public static final TagKey<Item> LENS = createTag("lens");
        public static final TagKey<Item> DIGESTABLE = createTag("digestable");
        public static final TagKey<Item> BIOPOLYMER = createTag("biopolymer");



        private static TagKey<Item> createTag(String name) {
            return TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(Cambium.MOD_ID, name));
        }
    }
}