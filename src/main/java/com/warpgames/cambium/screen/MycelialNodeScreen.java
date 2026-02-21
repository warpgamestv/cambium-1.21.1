package com.warpgames.cambium.screen;

import com.warpgames.cambium.network.MycelialNetworkExtractPayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import com.mojang.blaze3d.vertex.PoseStack;

import java.util.ArrayList;
import java.util.List;

public class MycelialNodeScreen extends AbstractContainerScreen<MycelialNodeMenu> {

    // Using vanilla generic_54 texture as placeholder
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath("minecraft",
            "textures/gui/container/generic_54.png");

    private List<ItemStack> networkItems = new ArrayList<>();
    private int scrollOffset = 0;
    private final int columns = 9;
    private final int rows = 5;

    public MycelialNodeScreen(MycelialNodeMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = 176;
        this.imageHeight = 222;
        this.titleLabelX = 8;
        this.titleLabelY = 6;
        this.inventoryLabelX = 8;
        this.inventoryLabelY = this.imageHeight - 94;
    }

    public void setNetworkItems(List<ItemStack> items) {
        this.networkItems = items;
    }

    @Override
    protected void init() {
        super.init();
        ClientPlayNetworking
                .send(new com.warpgames.cambium.network.MycelialNetworkRequestSyncPayload(this.menu.blockPos));
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        renderBackground(guiGraphics, mouseX, mouseY, delta);
        super.render(guiGraphics, mouseX, mouseY, delta);
        this.renderTooltip(guiGraphics, mouseX, mouseY);

        int startX = this.leftPos + 8;
        int startY = this.topPos + 18;
        int gridX = mouseX - startX;
        int gridY = mouseY - startY;
        int hoveredCol = gridX / 18;
        int hoveredRow = gridY / 18;

        for (int i = 0; i < rows * columns; i++) {
            int itemIndex = i + (scrollOffset * columns);
            if (itemIndex < networkItems.size()) {
                int col = i % columns;
                int row = i / columns;
                int drawX = startX + col * 18;
                int drawY = startY + row * 18;

                ItemStack stack = networkItems.get(itemIndex);
                guiGraphics.renderItem(stack, drawX, drawY);
                // Custom 0.5x scaled text rendering
                String text = formatCount(stack.getCount());
                PoseStack pose = guiGraphics.pose();
                pose.pushPose();

                // Anchor scale to the bottom right corner of the slot
                pose.translate(drawX + 16, drawY + 16, 0);
                pose.scale(0.5f, 0.5f, 1.0f);
                pose.translate(-(drawX + 16), -(drawY + 16), 0);

                // Let vanilla render the decorations with native depth handling, but inside our
                // scaled coordinate space
                guiGraphics.renderItemDecorations(this.font, stack, drawX, drawY, text);

                pose.popPose();

                if (gridX >= 0 && gridX < columns * 18 && gridY >= 0 && gridY < rows * 18) {
                    if (hoveredCol == col && hoveredRow == row && !stack.isEmpty()) {
                        guiGraphics.fill(drawX, drawY, drawX + 16, drawY + 16, 0x80FFFFFF);
                    }
                }
            }
        }
    }

    private String formatCount(int count) {
        if (count < 1000)
            return String.valueOf(count);
        if (count < 1000000)
            return String.format("%.1fk", count / 1000.0f);
        return String.format("%.1fm", count / 1000000.0f);
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;

        // 1.21.1 blit signature
        graphics.blit(TEXTURE, x, y, 0, 0, this.imageWidth, this.imageHeight);
    }

    // 1.21.1 uses the standard mouseClicked(double, double, int) signature
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int startX = this.leftPos + 8;
        int startY = this.topPos + 18;
        double gridX = mouseX - startX;
        double gridY = mouseY - startY;

        if (gridX >= 0 && gridX < columns * 18 && gridY >= 0 && gridY < rows * 18) {
            int hoveredCol = (int) (gridX / 18);
            int hoveredRow = (int) (gridY / 18);
            int i = hoveredCol + (hoveredRow * columns);
            int itemIndex = i + (scrollOffset * columns);

            if (itemIndex < networkItems.size() && !networkItems.get(itemIndex).isEmpty()) {
                int amount = button == 0 ? 64 : 1;
                ClientPlayNetworking.send(
                        new MycelialNetworkExtractPayload(this.menu.blockPos, networkItems.get(itemIndex), amount));
                return true;
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }
}
