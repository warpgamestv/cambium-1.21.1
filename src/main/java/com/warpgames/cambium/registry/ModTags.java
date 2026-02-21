package com.warpgames.cambium.registry;

import com.warpgames.cambium.Cambium;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class ModTags {
    public static class Items {
        public static final TagKey<Item> LENS = createTag("lens");
        public static final TagKey<Item> DIGESTABLE = createTag("digestable");
        public static final TagKey<Item> BIOPOLYMER = createTag("biopolymer");

        private static TagKey<Item> createTag(String name) {
            return TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(Cambium.MOD_ID, name));
        }
    }

    public static class Blocks {
        public static final TagKey<Block> MYCELIAL_CONNECTABLE_STORAGE = createTag("mycelial_connectable_storage");

        private static TagKey<Block> createTag(String name) {
            return TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath(Cambium.MOD_ID, name));
        }
    }
}