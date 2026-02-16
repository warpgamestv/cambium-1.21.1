package com.warpgames.cambium.registry;

import net.fabricmc.loader.api.FabricLoader;
import com.warpgames.cambium.content.ResourceTree;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class TreeRegistry {
    public static final List<ResourceTree> TREES = new ArrayList<>();

    // --- VANILLA ---
    public static final ResourceTree IRON = register("iron", 0xE0D3DE, () -> Items.RAW_IRON, "c:raw_materials/iron");
    public static final ResourceTree GOLD = register("gold", 0xFFA500, () -> Items.RAW_GOLD, "c:raw_materials/gold");
    public static final ResourceTree COPPER = register("copper", 0xB87333, () -> Items.RAW_COPPER, "c:raw_materials/copper");
    public static final ResourceTree DIAMOND = register("diamond", 0xA9C5A0, () -> Items.DIAMOND, "c:gems/diamond");
    public static final ResourceTree EMERALD = register("emerald", 0x4B644A, () -> Items.EMERALD, "c:gems/emerald");
    public static final ResourceTree REDSTONE = register("redstone", 0xFF0000, () -> Items.REDSTONE, "c:dusts/redstone");
    public static final ResourceTree LAPIS = register("lapis", 0x0000FF, () -> Items.LAPIS_LAZULI, "c:gems/lapis");

    // --- MODDED ---
    public static final ResourceTree BIOPOLYMER = register("biopolymer", 0xABA884, "cambium:biopolymer", "cambium:biopolymer");
    public static final ResourceTree VESPERITE = register("vesperite", 0x4B367C, "vesper_wilds:raw_vesperite", "c:raw_materials/vesperite");

    public static void init() {}

    private static ResourceTree register(String name, int color, Supplier<Item> drop, String tagName) {
        ResourceTree tree = new ResourceTree(name, "minecraft", color, drop, "minecraft:" + name, tagName);
        TREES.add(tree);
        return tree;
    }

    private static ResourceTree register(String name, int color, String itemId, String tagName) {
        String[] parts = itemId.split(":");
        String modId = parts.length > 0 ? parts[0] : "minecraft";

        Supplier<Item> dropSupplier = () -> {
            ResourceLocation id = ResourceLocation.tryParse(itemId);
            if (id == null) return Items.AIR;

            // FIX: BuiltInRegistries.ITEM.get(id) returns the Item directly.
            // If the item is missing, it returns Items.AIR automatically.
            return BuiltInRegistries.ITEM.get(id);
        };

        ResourceTree tree = new ResourceTree(name, modId, color, dropSupplier, itemId, tagName);

        boolean isModLoaded = FabricLoader.getInstance().isModLoaded(modId);
        boolean isCore = modId.equals("minecraft") || modId.equals("cambium");
        boolean isDataGen = System.getProperty("fabric-api.datagen") != null;

        if (isModLoaded || isCore || isDataGen) {
            TREES.add(tree);
        }
        return tree;
    }
}