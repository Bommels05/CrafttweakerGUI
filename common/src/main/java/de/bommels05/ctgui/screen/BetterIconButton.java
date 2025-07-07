package de.bommels05.ctgui.screen;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class BetterIconButton extends Button {
    private final ResourceLocation sprite;
    private final int spriteWidth;
    private final int spriteHeight;

    public BetterIconButton(int width, int height, ResourceLocation icon, int iconWidth, int iconHeight, OnPress onPress) {
        super(0, 0, width, height, Component.empty(), onPress, supplier -> Component.empty());
        this.sprite = icon;
        this.spriteWidth = iconWidth;
        this.spriteHeight = iconHeight;
    }

    @Override
    public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.renderWidget(graphics, mouseX, mouseY, partialTick);
        int x = this.getX() + this.getWidth() / 2 - this.spriteWidth / 2;
        int y = this.getY() + this.getHeight() / 2 - this.spriteHeight / 2;
        graphics.blit(this.sprite, x, y, 0, 0, this.spriteWidth, this.spriteHeight, this.spriteWidth, this.spriteHeight);
    }
}
