package de.bommels05.ctgui.screen;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

public class ColoredButton extends Button {

    private final int color;

    public ColoredButton(int x, int y, int width, int height, Component text, int color, OnPress onPress) {
        super(x, y, width, height, text, onPress, Button.DEFAULT_NARRATION);
        this.color = color;
    }

    public ColoredButton(int x, int y, int width, int height, Component text, int color, OnPress onPress, boolean visible) {
        this(x, y, width, height, text, color, onPress);
        this.visible = visible;
    }

    @Override
    public void renderString(GuiGraphics graphics, Font font, int color) {
        super.renderString(graphics, font, this.color);
    }
}
