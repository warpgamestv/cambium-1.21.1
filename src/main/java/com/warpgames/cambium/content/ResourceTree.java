package com.warpgames.cambium.content;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import java.util.function.Supplier;

public class ResourceTree {
    private final String name;
    private final String modId;
    private final int color;
    private final Supplier<Item> itemSupplier;
    private final String rawItemId;
    private final TagKey<Item> ingredientTag;

    private Block log;
    private Block leaves;
    private Block fruit;
    private Block sapling;

    public String getName() { return name; }
    public String getModId() { return modId; }
    public int getColor() { return color; }
    public Item getItem() { return itemSupplier.get(); }
    public String getRawItemId() { return rawItemId; }
    public TagKey<Item> getIngredientTag() { return ingredientTag; }

    // Constructor 1: Direct Supplier (Vanilla/Local)
    public ResourceTree(String name, String modId, int color, Supplier<Item> itemSupplier, String rawItemId, String tagName) {
        this.name = name;
        this.modId = modId;
        this.color = color;
        this.itemSupplier = itemSupplier;
        this.rawItemId = rawItemId;

        if (tagName != null && !tagName.isEmpty()) {
            this.ingredientTag = TagKey.create(Registries.ITEM, ResourceLocation.parse(tagName));
        } else {
            this.ingredientTag = null;

        }
    }

    // --- SETTERS ---
    public void setLog(Block log) { this.log = log; }
    public Block getLog() { return log; }

    public void setLeaves(Block leaves) { this.leaves = leaves; }
    public Block getLeaves() { return leaves; }

    public void setFruit(Block fruit) { this.fruit = fruit; }
    public Block getFruit() { return fruit; }

    public void setSapling(Block sapling) {this.sapling = sapling; }
    public Block getSapling() {return sapling; }
}