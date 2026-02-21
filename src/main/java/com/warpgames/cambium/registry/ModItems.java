package com.warpgames.cambium.registry;

import com.warpgames.cambium.Cambium;
import com.warpgames.cambium.item.ModArmorMaterials;
import com.warpgames.cambium.item.custom.GraftingToolItem;
import com.warpgames.cambium.item.custom.PhotovoltaicArmorItem;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;

import java.util.function.Function;

public class ModItems {

    public static final Item SOLAR_LENS = registerItem("solar_lens", Item::new, new Item.Properties().stacksTo(1));
    public static final Item ORGANIC_ASH = registerItem("organic_ash", Item::new, new Item.Properties());
    public static final Item BIOCOMPOSITE_PASTE = registerItem("biocomposite_paste", Item::new, new Item.Properties());
    public static final Item BIOPOLYMER = registerItem("biopolymer", Item::new, new Item.Properties());
    public static final Item BIOPOLYMER_CASING = registerItem("biopolymer_casing", Item::new, new Item.Properties());
    public static final Item GRAFTING_TOOL = registerItem("grafting_tool", GraftingToolItem::new,
            new Item.Properties().stacksTo(1));

    public static final Item PHOTOVOLTAIC_HELMET = registerItem("photovoltaic_helmet",
            properties -> new PhotovoltaicArmorItem(ModArmorMaterials.PHOTOVOLTAIC, ArmorItem.Type.HELMET, properties),
            new Item.Properties().durability(ArmorItem.Type.HELMET.getDurability(15)));

    public static final Item PHOTOVOLTAIC_CHESTPLATE = registerItem("photovoltaic_chestplate",
            properties -> new PhotovoltaicArmorItem(ModArmorMaterials.PHOTOVOLTAIC, ArmorItem.Type.CHESTPLATE,
                    properties),
            new Item.Properties().durability(ArmorItem.Type.CHESTPLATE.getDurability(15)));

    public static final Item PHOTOVOLTAIC_LEGGINGS = registerItem("photovoltaic_leggings",
            properties -> new PhotovoltaicArmorItem(ModArmorMaterials.PHOTOVOLTAIC, ArmorItem.Type.LEGGINGS,
                    properties),
            new Item.Properties().durability(ArmorItem.Type.LEGGINGS.getDurability(15)));

    public static final Item PHOTOVOLTAIC_BOOTS = registerItem("photovoltaic_boots",
            properties -> new PhotovoltaicArmorItem(ModArmorMaterials.PHOTOVOLTAIC, ArmorItem.Type.BOOTS, properties),
            new Item.Properties().durability(ArmorItem.Type.BOOTS.getDurability(15)));

    private static Item registerItem(String name, Function<Item.Properties, Item> factory, Item.Properties properties) {
        // 1. Create the ResourceKey
        ResourceKey<Item> key = ResourceKey.create(Registries.ITEM,
                ResourceLocation.fromNamespaceAndPath(Cambium.MOD_ID, name));

        // 2. Register
        return Registry.register(BuiltInRegistries.ITEM, key, factory.apply(properties));
    }

    public static void registerModItems() {
        Cambium.LOGGER.info("Registering Mod Items for " + Cambium.MOD_ID);
    }
}