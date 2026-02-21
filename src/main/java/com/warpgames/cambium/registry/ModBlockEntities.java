package com.warpgames.cambium.registry;

import com.warpgames.cambium.Cambium;
import com.warpgames.cambium.block.entity.*;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class ModBlockEntities {

        public static final BlockEntityType<MineralSoilBlockEntity> MINERAL_SOIL_BE = Registry.register(
                        BuiltInRegistries.BLOCK_ENTITY_TYPE,
                        ResourceLocation.fromNamespaceAndPath("cambium", "mineral_soil_be"),
                        FabricBlockEntityTypeBuilder.create(MineralSoilBlockEntity::new, ModBlocks.MINERAL_SOIL)
                                        .build(null));

        public static final BlockEntityType<SolarDigesterBlockEntity> SOLAR_DIGESTER_BE = Registry.register(
                        BuiltInRegistries.BLOCK_ENTITY_TYPE,
                        ResourceLocation.fromNamespaceAndPath("cambium", "solar_digester_be"),
                        FabricBlockEntityTypeBuilder.create(SolarDigesterBlockEntity::new, ModBlocks.SOLAR_DIGESTER)
                                        .build(null));

        public static final BlockEntityType<RootBlockEntity> ROOT_BE = Registry.register(
                        BuiltInRegistries.BLOCK_ENTITY_TYPE,
                        ResourceLocation.fromNamespaceAndPath("cambium", "root_be"),
                        FabricBlockEntityTypeBuilder.create(RootBlockEntity::new, ModBlocks.ROOT_BLOCK).build(null));

        public static final BlockEntityType<SolarConcentratorBlockEntity> SOLAR_CONCENTRATOR_BE = Registry.register(
                        BuiltInRegistries.BLOCK_ENTITY_TYPE,
                        ResourceLocation.fromNamespaceAndPath("cambium", "solar_concentrator_be"),
                        FabricBlockEntityTypeBuilder
                                        .create(SolarConcentratorBlockEntity::new, ModBlocks.SOLAR_CONCENTRATOR)
                                        .build(null));

        public static final BlockEntityType<GravitropicNodeBlockEntity> GRAVITROPIC_NODE_BE = Registry.register(
                        BuiltInRegistries.BLOCK_ENTITY_TYPE,
                        ResourceLocation.fromNamespaceAndPath("cambium", "gravitropic_node_be"),
                        FabricBlockEntityTypeBuilder.create(GravitropicNodeBlockEntity::new, ModBlocks.GRAVITROPIC_NODE)
                                        .build(null));
        public static final BlockEntityType<PhloemDuctBlockEntity> PHLOEM_DUCT = Registry.register(
                        BuiltInRegistries.BLOCK_ENTITY_TYPE,
                        ResourceLocation.fromNamespaceAndPath("cambium", "phloem_duct"),
                        FabricBlockEntityTypeBuilder.create(PhloemDuctBlockEntity::new, ModBlocks.PHLOEM_DUCT)
                                        .build(null));

        public static final BlockEntityType<MycelialNodeBlockEntity> MYCELIAL_NODE_BE = Registry.register(
                        BuiltInRegistries.BLOCK_ENTITY_TYPE,
                        ResourceLocation.fromNamespaceAndPath("cambium", "mycelial_node_be"),
                        FabricBlockEntityTypeBuilder.create(MycelialNodeBlockEntity::new, ModBlocks.MYCELIAL_NODE)
                                        .build(null));

        public static void registerBlockEntities() {
                Cambium.LOGGER.info("Registering Block Entities for " + Cambium.MOD_ID);
        }
}