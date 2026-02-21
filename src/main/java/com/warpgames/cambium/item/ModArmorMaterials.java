package com.warpgames.cambium.item;

import com.warpgames.cambium.Cambium;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.tags.ItemTags;

import java.util.EnumMap;
import java.util.List;
import java.util.function.Supplier;

public class ModArmorMaterials {

    public static final Holder<ArmorMaterial> PHOTOVOLTAIC = register("photovoltaic",
            Util.make(new EnumMap<>(ArmorItem.Type.class), map -> {
                map.put(ArmorItem.Type.BOOTS, 3);
                map.put(ArmorItem.Type.LEGGINGS, 5);
                map.put(ArmorItem.Type.CHESTPLATE, 7);
                map.put(ArmorItem.Type.HELMET, 3);
                map.put(ArmorItem.Type.BODY, 9);
            }),
            15, // Enchantability
            SoundEvents.ARMOR_EQUIP_IRON,
            0.0F, // Toughness
            0.0F, // Knockback Resistance
            () -> Ingredient.of(ItemTags.COPPER_ORES),
            15 // Durability multiplier
    );

    private static Holder<ArmorMaterial> register(String name,
            EnumMap<ArmorItem.Type, Integer> defense,
            int enchantability,
            Holder<net.minecraft.sounds.SoundEvent> equipSound,
            float toughness,
            float knockbackResistance,
            Supplier<Ingredient> repairIngredient,
            int durabilityMultiplier) {

        // In 1.21.1, ArmorMaterial is registered to get a Holder
        List<ArmorMaterial.Layer> layers = List.of(
                new ArmorMaterial.Layer(ResourceLocation.fromNamespaceAndPath(Cambium.MOD_ID, name)));

        EnumMap<ArmorItem.Type, Integer> durabilityMap = new EnumMap<>(ArmorItem.Type.class);
        for (ArmorItem.Type type : ArmorItem.Type.values()) {
            durabilityMap.put(type, defense.getOrDefault(type, 0));
        }

        ArmorMaterial material = new ArmorMaterial(
                durabilityMap,
                enchantability,
                equipSound,
                repairIngredient,
                layers,
                toughness,
                knockbackResistance);

        return Registry.registerForHolder(
                BuiltInRegistries.ARMOR_MATERIAL,
                ResourceLocation.fromNamespaceAndPath(Cambium.MOD_ID, name),
                material);
    }
}
