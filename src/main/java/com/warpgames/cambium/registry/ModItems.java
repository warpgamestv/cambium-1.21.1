package com.warpgames.cambium.registry;

import com.warpgames.cambium.Cambium;
import com.warpgames.cambium.item.custom.GraftingToolItem;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;

import java.util.function.Function;

public class ModItems {

    public static final Item SOLAR_LENS = registerItem("solar_lens", Item::new, new Item.Properties().stacksTo(1));
    public static final Item ORGANIC_ASH = registerItem("organic_ash", Item::new, new Item.Properties());
    public static final Item BIOCOMPOSITE_PASTE = registerItem("biocomposite_paste", Item::new, new Item.Properties());
    public static final Item BIOPOLYMER = registerItem("biopolymer", Item::new, new Item.Properties());
    public static final Item BIOPOLYMER_CASING = registerItem("biopolymer_casing", Item::new, new Item.Properties());
    public static final Item GRAFTING_TOOL = registerItem("grafting_tool", GraftingToolItem::new, new Item.Properties().stacksTo(1));

    private static Item registerItem(String name, Function<Item.Properties, Item> factory, Item.Properties properties) {
        // 1. Create the ResourceKey
        ResourceKey<Item> key = ResourceKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(Cambium.MOD_ID, name));

        // 2. Register (Remove .setId, it doesn't exist/isn't needed in 1.21.1 properties)
        return Registry.register(BuiltInRegistries.ITEM, key, factory.apply(properties));
    }

    public static void registerModItems() {
        Cambium.LOGGER.info("Registering Mod Items for " + Cambium.MOD_ID);
    }
}