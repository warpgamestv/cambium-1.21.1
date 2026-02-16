package com.warpgames.cambium.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.warpgames.cambium.Cambium;
import com.warpgames.cambium.menu.SolarConcentratorMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class SolarConcentratorScreen extends AbstractContainerScreen<SolarConcentratorMenu> {

    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(Cambium.MOD_ID, "textures/gui/solar_concentrator.png");

    public SolarConcentratorScreen(SolarConcentratorMenu handler, Inventory inventory, Component title) {
        super(handler, inventory, title);
    }

    @Override
    protected void init() {
        super.init();
        this.inventoryLabelY = 72;
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        renderBackground(guiGraphics, mouseX, mouseY, delta);
        super.render(guiGraphics, mouseX, mouseY, delta);
        renderTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        // 1. Draw Background
        // FIX: Removed RenderPipelines arg. Standard 1.21.1 blit signature:
        // blit(texture, x, y, u, v, width, height)
        graphics.blit(TEXTURE, x, y, 0, 0, imageWidth, imageHeight);

        // 2. Draw Progress Arrow
        int arrowWidth = this.menu.getScaledProgress();

        if (arrowWidth > 0) {
            // FIX: 1.21.1 signature
            graphics.blit(
                    TEXTURE,        // Texture
                    x + 79, y + 42, // Screen X, Y
                    176, 14,        // Texture U, V
                    arrowWidth, 17  // Width, Height
            );
        }
    }
}