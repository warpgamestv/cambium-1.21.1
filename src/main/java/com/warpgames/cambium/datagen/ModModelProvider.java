package com.warpgames.cambium.datagen;

import com.warpgames.cambium.Cambium;
import com.warpgames.cambium.block.ResourceFruitBlock;
import com.warpgames.cambium.content.ResourceTree;
import com.warpgames.cambium.registry.ModBlocks;
import com.warpgames.cambium.registry.ModItems;
import com.warpgames.cambium.registry.TreeRegistry;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.data.models.BlockModelGenerators;
import net.minecraft.data.models.ItemModelGenerators;
import net.minecraft.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.data.models.blockstates.PropertyDispatch;
import net.minecraft.data.models.blockstates.Variant;
import net.minecraft.data.models.blockstates.VariantProperties;
import net.minecraft.data.models.model.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;

import java.util.Optional;

public class ModModelProvider extends FabricModelProvider {

        public static final TextureSlot STEM_SLOT = TextureSlot.create("stem");
        public static final TextureSlot LEAVES_SLOT = TextureSlot.create("leaves");

        public static final ModelTemplate TINTED_LEAVES = new ModelTemplate(
                        Optional.of(ResourceLocation.withDefaultNamespace("block/leaves")), Optional.empty(),
                        TextureSlot.ALL);
        public static final ModelTemplate TINTED_CROSS_OVERLAY = new ModelTemplate(
                        Optional.of(ResourceLocation.fromNamespaceAndPath(Cambium.MOD_ID,
                                        "block/tinted_cross_overlay")),
                        Optional.empty(), STEM_SLOT, LEAVES_SLOT);

        public static final ModelTemplate FRUIT_STAGE0 = new ModelTemplate(
                        Optional.of(ResourceLocation.fromNamespaceAndPath(Cambium.MOD_ID, "block/base_fruit_stage0")),
                        Optional.empty(), TextureSlot.ALL);
        public static final ModelTemplate FRUIT_STAGE1 = new ModelTemplate(
                        Optional.of(ResourceLocation.fromNamespaceAndPath(Cambium.MOD_ID, "block/base_fruit_stage1")),
                        Optional.empty(), TextureSlot.ALL);
        public static final ModelTemplate FRUIT_STAGE2 = new ModelTemplate(
                        Optional.of(ResourceLocation.fromNamespaceAndPath(Cambium.MOD_ID, "block/base_fruit_stage2")),
                        Optional.empty(), TextureSlot.ALL);

        public ModModelProvider(FabricDataOutput output) {
                super(output);
        }

        @Override
        public void generateBlockStateModels(BlockModelGenerators generator) {
                // --- STATIC BLOCKS ---
                generator.createTrivialCube(ModBlocks.MINERAL_SOIL);
                generator.createTrivialCube(ModBlocks.GRAVITROPIC_NODE);

                // Mycelial Node uses a hand-crafted Blockbench model in src/main/resources
                ResourceLocation mycelialNodeModel = ResourceLocation.fromNamespaceAndPath(Cambium.MOD_ID,
                                "block/mycelial_node");
                generator.blockStateOutput.accept(
                                BlockModelGenerators.createSimpleBlock(ModBlocks.MYCELIAL_NODE, mycelialNodeModel));

                // MYCELIAL_STRAND uses hand-crafted multipart blockstate + models in
                // src/main/resources — do NOT generate a trivial cube here.
                generator.woodProvider(ModBlocks.LIVING_LOG).logWithHorizontal(ModBlocks.LIVING_LOG);

                ResourceLocation rootSide = ResourceLocation.fromNamespaceAndPath(Cambium.MOD_ID, "block/root_block");
                ResourceLocation rootTop = ResourceLocation.fromNamespaceAndPath(Cambium.MOD_ID,
                                "block/root_block_top");
                ResourceLocation rootModel = ModelTemplates.CUBE_COLUMN.create(ModBlocks.ROOT_BLOCK,
                                TextureMapping.column(rootSide, rootTop), generator.modelOutput);
                generator.blockStateOutput
                                .accept(BlockModelGenerators.createSimpleBlock(ModBlocks.ROOT_BLOCK, rootModel));

                // --- RESOURCE TREES ---
                for (ResourceTree tree : TreeRegistry.TREES) {
                        // Leaves
                        TextureMapping leavesMapping = new TextureMapping().put(TextureSlot.ALL,
                                        ResourceLocation.fromNamespaceAndPath(Cambium.MOD_ID, "block/living_leaves"));
                        ResourceLocation leavesModel = TINTED_LEAVES.create(tree.getLeaves(), leavesMapping,
                                        generator.modelOutput);
                        generator.blockStateOutput.accept(MultiVariantGenerator.multiVariant(tree.getLeaves(),
                                        Variant.variant().with(VariantProperties.MODEL, leavesModel)));

                        // Fruit
                        ResourceLocation model0 = FRUIT_STAGE0.createWithSuffix(tree.getFruit(), "_stage0",
                                        new TextureMapping().put(TextureSlot.ALL,
                                                        ResourceLocation.fromNamespaceAndPath(Cambium.MOD_ID,
                                                                        "block/base_fruit_stage0")),
                                        generator.modelOutput);
                        ResourceLocation model1 = FRUIT_STAGE1.createWithSuffix(tree.getFruit(), "_stage1",
                                        new TextureMapping().put(TextureSlot.ALL,
                                                        ResourceLocation.fromNamespaceAndPath(Cambium.MOD_ID,
                                                                        "block/base_fruit_stage1")),
                                        generator.modelOutput);
                        ResourceLocation model2 = FRUIT_STAGE2.createWithSuffix(tree.getFruit(), "_stage2",
                                        new TextureMapping().put(TextureSlot.ALL,
                                                        ResourceLocation.fromNamespaceAndPath(Cambium.MOD_ID,
                                                                        "block/base_fruit_stage2")),
                                        generator.modelOutput);
                        generator.blockStateOutput.accept(MultiVariantGenerator.multiVariant(tree.getFruit())
                                        .with(PropertyDispatch.property(ResourceFruitBlock.AGE)
                                                        .select(0, Variant.variant().with(VariantProperties.MODEL,
                                                                        model0))
                                                        .select(1, Variant.variant().with(VariantProperties.MODEL,
                                                                        model1))
                                                        .select(2, Variant.variant().with(VariantProperties.MODEL,
                                                                        model2))));

                        // Saplings
                        TextureMapping saplingMap = new TextureMapping()
                                        .put(STEM_SLOT, ResourceLocation.fromNamespaceAndPath(Cambium.MOD_ID,
                                                        "block/sapling_stem"))
                                        .put(LEAVES_SLOT, ResourceLocation.fromNamespaceAndPath(Cambium.MOD_ID,
                                                        "block/sapling_leaves"));
                        ResourceLocation saplingModel = TINTED_CROSS_OVERLAY.create(tree.getSapling(), saplingMap,
                                        generator.modelOutput);
                        generator.blockStateOutput.accept(
                                        BlockModelGenerators.createSimpleBlock(tree.getSapling(), saplingModel));
                }
        }

        @Override
        public void generateItemModels(ItemModelGenerators generator) {
                // Generic Items
                generator.generateFlatItem(ModItems.ORGANIC_ASH, ModelTemplates.FLAT_ITEM);
                generator.generateFlatItem(ModItems.SOLAR_LENS, ModelTemplates.FLAT_ITEM);
                generator.generateFlatItem(ModItems.BIOCOMPOSITE_PASTE, ModelTemplates.FLAT_ITEM);
                generator.generateFlatItem(ModItems.BIOPOLYMER, ModelTemplates.FLAT_ITEM);
                generator.generateFlatItem(ModItems.BIOPOLYMER_CASING, ModelTemplates.FLAT_ITEM);
                generator.generateFlatItem(ModItems.GRAFTING_TOOL, ModelTemplates.FLAT_ITEM);
                generator.generateFlatItem(ModItems.PHOTOVOLTAIC_HELMET, ModelTemplates.FLAT_ITEM);
                generator.generateFlatItem(ModItems.PHOTOVOLTAIC_CHESTPLATE, ModelTemplates.FLAT_ITEM);
                generator.generateFlatItem(ModItems.PHOTOVOLTAIC_LEGGINGS, ModelTemplates.FLAT_ITEM);
                generator.generateFlatItem(ModItems.PHOTOVOLTAIC_BOOTS, ModelTemplates.FLAT_ITEM);

                for (ResourceTree tree : TreeRegistry.TREES) {
                        // A. SAPLINGS (Layered Item Model: Stitching Stem and Leaves)
                        // Arg 1: Target Model ID (e.g., cambium:item/iron_sapling)
                        // Arg 2: Layer 0 (Leaves - Gets tinted by color provider)
                        // Arg 3: Layer 1 (Stem - Static color)
                        generator.generateLayeredItem(
                                        ModelLocationUtils.getModelLocation(tree.getSapling().asItem()),
                                        ResourceLocation.fromNamespaceAndPath(Cambium.MOD_ID, "block/sapling_leaves"),
                                        ResourceLocation.fromNamespaceAndPath(Cambium.MOD_ID, "block/sapling_stem"));

                        // B. LEAVES (3D Block in Inventory)
                        ResourceLocation leafBlockModel = ModelLocationUtils.getModelLocation(tree.getLeaves());
                        new ModelTemplate(Optional.of(leafBlockModel), Optional.empty()).create(
                                        ModelLocationUtils.getModelLocation(tree.getLeaves().asItem()),
                                        new TextureMapping(),
                                        generator.output);

                        // C. FRUIT (3D Block in Inventory)
                        ResourceLocation fruitBlockModel = ModelLocationUtils.getModelLocation(tree.getFruit(),
                                        "_stage2");
                        new ModelTemplate(Optional.of(fruitBlockModel), Optional.empty()).create(
                                        ModelLocationUtils.getModelLocation(tree.getFruit().asItem()),
                                        new TextureMapping(),
                                        generator.output);
                }

                // --- Custom Block Items ---
                ResourceLocation mycelialNodeModel = ResourceLocation.fromNamespaceAndPath(Cambium.MOD_ID,
                                "block/mycelial_node");
                new ModelTemplate(Optional.of(mycelialNodeModel), Optional.empty()).create(
                                ModelLocationUtils.getModelLocation(ModBlocks.MYCELIAL_NODE.asItem()),
                                new TextureMapping(),
                                generator.output);

                ResourceLocation mycelialStrandModel = ResourceLocation.fromNamespaceAndPath(Cambium.MOD_ID,
                                "block/mycelial_strand_core");
                new ModelTemplate(Optional.of(mycelialStrandModel), Optional.empty()).create(
                                ModelLocationUtils.getModelLocation(ModBlocks.MYCELIAL_STRAND.asItem()),
                                new TextureMapping(),
                                generator.output);
        }
}