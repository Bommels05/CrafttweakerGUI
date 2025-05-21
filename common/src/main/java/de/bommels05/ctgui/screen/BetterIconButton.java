package de.bommels05.ctgui.screen;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.SpriteIconButton;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class BetterIconButton extends SpriteIconButton {
    public BetterIconButton(int width, int height, ResourceLocation icon, int iconWidth, int iconHeight, OnPress onPress) {
        super(width, height, Component.empty(), iconWidth, iconHeight, icon, onPress, null);
    }

    @Override
    public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.renderWidget(graphics, mouseX, mouseY, partialTick);
        int x = this.getX() + this.getWidth() / 2 - this.spriteWidth / 2;
        int y = this.getY() + this.getHeight() / 2 - this.spriteHeight / 2;
        graphics.blit(this.sprite, x, y, 0, 0, this.spriteWidth, this.spriteHeight, this.spriteWidth, this.spriteHeight);
    }
}
