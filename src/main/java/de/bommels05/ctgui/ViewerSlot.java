package de.bommels05.ctgui;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.item.ItemStack;

public interface ViewerSlot {

    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick);

    public void renderTooltip(Screen screen, GuiGraphics graphics, int mouseX, int mouseY);

    public boolean mouseOver(int mouseX, int mouseY);

    public ItemStack getStack();

}
