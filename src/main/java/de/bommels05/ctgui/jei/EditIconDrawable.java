package de.bommels05.ctgui.jei;

import de.bommels05.ctgui.CraftTweakerGUI;
import mezz.jei.api.gui.drawable.IDrawable;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;

public class EditIconDrawable implements IDrawable {

    private static final ResourceLocation ICON = new ResourceLocation(CraftTweakerGUI.MOD_ID, "textures/gui/edit_icon.png");

    @Override
    public int getWidth() {
        return 0;
    }

    @Override
    public int getHeight() {
        return 0;
    }

    @Override
    public void draw(GuiGraphics graphics) {
        graphics.pose().translate(-5.5, -5.5, 0);
        draw(graphics, 0, 0);
    }

    @Override
    public void draw(GuiGraphics graphics, int xOffset, int yOffset) {
        graphics.pose().translate(1, 1, 0);
        graphics.blit(ICON, xOffset , yOffset, 0, 0, 9, 9, 9, 9);
    }
}
