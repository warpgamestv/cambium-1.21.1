package com.warpgames.cambium.client.screen;

import com.warpgames.cambium.Cambium;
import com.warpgames.cambium.menu.SolarDigesterMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class SolarDigesterScreen extends AbstractContainerScreen<SolarDigesterMenu> {

    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(Cambium.MOD_ID, "textures/gui/solar_digester.png");

    public SolarDigesterScreen(SolarDigesterMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
    }

    @Override
    protected void init() {
        super.init();
        this.titleLabelX = (this.imageWidth - this.font.width(this.title)) / 2;
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(graphics, mouseX, mouseY, partialTick);
        super.render(graphics, mouseX, mouseY, partialTick);
        this.renderTooltip(graphics, mouseX, mouseY);
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;

        // 1. Draw Background
        // FIX: Use standard 1.21.1 blit
        graphics.blit(TEXTURE, x, y, 0, 0, imageWidth, imageHeight);

        // 2. Draw Arrow
        int arrowWidth = this.menu.getScaledProgress();

        if (arrowWidth > 0) {
            // FIX: Use standard 1.21.1 blit
            graphics.blit(
                    TEXTURE,
                    x + 79, y + 42,
                    176, 14,
                    arrowWidth, 17
            );
        }
    }
}