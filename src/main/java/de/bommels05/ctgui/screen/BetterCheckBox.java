package de.bommels05.ctgui.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import de.bommels05.ctgui.CraftTweakerGUI;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Consumer;

public class BetterCheckBox extends AbstractButton {

    public static final int SIZE = 20;
    public static final ResourceLocation TEXTURE = new ResourceLocation(CraftTweakerGUI.MOD_ID, "textures/gui/checkbox.png");
    private boolean selected;
    private Consumer<Boolean> handler;

    public BetterCheckBox(int x, int y, Component name, Font font, boolean selected, Consumer<Boolean> handler) {
        super(x, y, SIZE + 4 + font.width(name),    SIZE, name);
        this.selected = selected;
        this.handler = handler;
    }

    public BetterCheckBox(int x, int y, Component name, Consumer<Boolean> handler) {
        this(x, y, name, Minecraft.getInstance().font, false, handler);
    }

    @Override
    public void onPress() {
        this.selected = !this.selected;
        this.handler.accept(this.selected);
    }

    @Override
    public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        RenderSystem.enableDepthTest();
        Font font = Minecraft.getInstance().font;
        graphics.setColor(1.0F, 1.0F, 1.0F, this.alpha);
        RenderSystem.enableBlend();
        int offset;
        if (this.selected) {
            offset = this.isFocused() ? SIZE * 3 : SIZE * 2;
        } else {
            offset = this.isFocused() ? SIZE : 0;
        }

        int nameX = this.getX() + SIZE + 4;
        int nameY = this.getY() + (this.height >> 1) - (9 >> 1);
        graphics.blit(TEXTURE, this.getX(), this.getY(), offset, 0, SIZE, SIZE, 80, 20);
        graphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
        graphics.drawString(font, this.getMessage(), nameX, nameY, 16777215);
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {}
}
