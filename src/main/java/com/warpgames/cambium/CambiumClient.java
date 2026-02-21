package com.warpgames.cambium;

import com.warpgames.cambium.client.renderer.PhloemDuctRenderer;
import com.warpgames.cambium.client.screen.SolarConcentratorScreen;
import com.warpgames.cambium.client.screen.SolarDigesterScreen;
import com.warpgames.cambium.content.ResourceTree;
import com.warpgames.cambium.registry.ModBlockEntities;
import com.warpgames.cambium.registry.ModBlocks;
import com.warpgames.cambium.registry.ModScreenHandlers;
import com.warpgames.cambium.registry.TreeRegistry;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap; // Correct Import
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.RenderType; // Correct Import
import net.minecraft.world.level.block.Block;

@Environment(EnvType.CLIENT)
public class CambiumClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        Cambium.LOGGER.info("Client Init: Setting up Block Colors and Render Layers.");

        for (ResourceTree tree : TreeRegistry.TREES) {
            Block leaves = tree.getLeaves();
            Block sapling = tree.getSapling();
            Block fruit = tree.getFruit();

            // --- A. LEAVES ---
            if (leaves != null) {
                // Block Colors (World)
                ColorProviderRegistry.BLOCK.register((state, view, pos, tintIndex) -> tree.getColor(), leaves);

                // Item Colors (Inventory) - Important for Item rendering!
                ColorProviderRegistry.ITEM.register((stack, tintIndex) -> tree.getColor(), leaves);

                // Transparency
                BlockRenderLayerMap.INSTANCE.putBlock(leaves, RenderType.cutout());
            }

            // --- B. SAPLINGS ---
            if (sapling != null) {
                // Block Colors
                ColorProviderRegistry.BLOCK.register((state, view, pos, tintIndex) -> {
                    return tintIndex == 0 ? tree.getColor() : -1;
                }, sapling);

                // Item Colors
                ColorProviderRegistry.ITEM.register((stack, tintIndex) -> {
                    return tintIndex == 0 ? tree.getColor() : -1;
                }, sapling);

                // Transparency
                BlockRenderLayerMap.INSTANCE.putBlock(sapling, RenderType.cutout());
            }

            // --- C. FRUIT ---
            if (fruit != null) {
                ColorProviderRegistry.BLOCK.register((state, view, pos, tintIndex) -> tree.getColor(), fruit);
                ColorProviderRegistry.ITEM.register((stack, tintIndex) -> tree.getColor(), fruit);
                BlockRenderLayerMap.INSTANCE.putBlock(fruit, RenderType.cutout());
            }
        }

        // --- Static Blocks Render Layers ---
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.ROOT_BLOCK, RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.MINERAL_SOIL, RenderType.cutout());

        // Phloem Duct is Glass-like, so use Translucent
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.PHLOEM_DUCT, RenderType.translucent());

        // --- Register Block Entity Renderers ---
        BlockEntityRendererRegistry.register(ModBlockEntities.PHLOEM_DUCT, PhloemDuctRenderer::new);

        // --- Register Screens ---
        MenuScreens.register(ModScreenHandlers.SOLAR_DIGESTER_MENU, SolarDigesterScreen::new);
        MenuScreens.register(ModScreenHandlers.SOLAR_CONCENTRATOR_SCREEN_HANDLER, SolarConcentratorScreen::new);
        MenuScreens.register(ModScreenHandlers.MYCELIAL_NODE_MENU,
                com.warpgames.cambium.screen.MycelialNodeScreen::new);

        // Mycelial Strand Render Layer
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.MYCELIAL_STRAND, RenderType.cutout());

        // --- Client Networking ---
        net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking.registerGlobalReceiver(
                com.warpgames.cambium.network.MycelialNetworkSyncPayload.ID,
                (payload, context) -> {
                    context.client().execute(() -> {
                        if (context.client().screen instanceof com.warpgames.cambium.screen.MycelialNodeScreen screen) {
                            screen.setNetworkItems(payload.items());
                        }
                    });
                });
    }
}