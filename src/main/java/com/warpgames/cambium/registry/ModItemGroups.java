package com.warpgames.cambium.registry;

import com.warpgames.cambium.Cambium;
import com.warpgames.cambium.content.ResourceTree;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import static com.warpgames.cambium.Cambium.MOD_ID;

public class ModItemGroups {

//Tab 1: General Cambium
    public static final CreativeModeTab CAMBIUM_TAB = Registry.register(
            BuiltInRegistries.CREATIVE_MODE_TAB,
            ResourceLocation.fromNamespaceAndPath(MOD_ID,"cambium_tab"),
            FabricItemGroup.builder()
                    .icon(() -> new ItemStack(ModBlocks.SOLAR_DIGESTER)) // The icon on the tab
                    .title(Component.translatable("itemGroup.cambium")) // The hover text
                    .displayItems((context, entries) -> {
                        // --- Add your items here ---
                        entries.accept(ModBlocks.SOLAR_DIGESTER);
                        entries.accept(ModBlocks.SOLAR_CONCENTRATOR);
                        entries.accept(ModBlocks.ROOT_BLOCK);
                        entries.accept(ModBlocks.MINERAL_SOIL);
                        entries.accept(ModBlocks.LIVING_LOG);
                        entries.accept(ModItems.SOLAR_LENS);
                        entries.accept(ModItems.ORGANIC_ASH);
                        entries.accept(ModItems.BIOCOMPOSITE_PASTE);
                        entries.accept(ModItems.BIOPOLYMER);
                        entries.accept(ModItems.BIOPOLYMER_CASING);
                        entries.accept(ModItems.GRAFTING_TOOL);
                        entries.accept(ModBlocks.GRAVITROPIC_NODE);
                        entries.accept(ModBlocks.PHLOEM_DUCT);
                    })
                    .build());

    // --- TAB 2: RESOURCE TREES (Leaves, Fruit, Saplings) ---
    public static final CreativeModeTab RESOURCE_TREES_TAB = Registry.register(
            BuiltInRegistries.CREATIVE_MODE_TAB,
            ResourceLocation.fromNamespaceAndPath(MOD_ID, "resource_trees_tab"),
            FabricItemGroup.builder()
                    .icon(() -> new ItemStack(TreeRegistry.EMERALD.getSapling())) //
                    .title(Component.translatable("itemGroup.cambium_trees"))
                    .displayItems((context, entries) -> {
                        for (ResourceTree tree : TreeRegistry.TREES) {
                            if (tree.getLeaves() != null) entries.accept(tree.getLeaves());
                            if (tree.getFruit() != null) entries.accept(tree.getFruit());

                            ResourceLocation saplingId = ResourceLocation.fromNamespaceAndPath(MOD_ID, tree.getName() + "_sapling");
                            BuiltInRegistries.ITEM.getOptional(saplingId).ifPresent(entries::accept);
                        }
                    })
                    .build());

    public static void registerItemGroups() {
        Cambium.LOGGER.info("Registering Item Groups for " + MOD_ID);
    }
}