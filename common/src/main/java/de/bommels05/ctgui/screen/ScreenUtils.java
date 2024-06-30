package de.bommels05.ctgui.screen;

import de.bommels05.ctgui.CraftTweakerGUI;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;

public class ScreenUtils {
    private static final ResourceLocation BACKGROUND_PARTS = new ResourceLocation(CraftTweakerGUI.MOD_ID, "textures/gui/background.png");

    public static void renderContainerBackground(GuiGraphics graphics, int x, int y, int width, int height) {
        graphics.fill(x + 3, y + 3, x + width - 3, y + height - 3, FastColor.ARGB32.color(255, 198, 198, 198));
        graphics.blit(BACKGROUND_PARTS, x, y, 0, 0, 0, 5, 5, 16, 16);
        for (int i = x + 5; i < x + width - 4; i++) {
            graphics.blit(BACKGROUND_PARTS, i, y, 0, 6, 0, 1, 4, 16, 16);
        }
        graphics.blit(BACKGROUND_PARTS, x + width - 4, y, 0, 9, 0, 4, 4, 16, 16);
        for (int i = y + 4; i < y + height - 5; i++) {
            graphics.blit(BACKGROUND_PARTS, x + width - 4, i, 0, 9, 5, 4, 1, 16, 16);
        }
        graphics.blit(BACKGROUND_PARTS, x + width - 5, y + height - 5, 0, 8, 7, 5, 5, 16, 16);
        for (int i = x + width - 5; i > x + 3; i--) {
            graphics.blit(BACKGROUND_PARTS, i, y + height - 4, 0, 6, 8, 1, 4, 16, 16);
        }
        graphics.blit(BACKGROUND_PARTS, x, y + height - 4, 0, 0, 8, 4, 4, 16, 16);
        for (int i = y + height - 4; i > y + 4; i--) {
            graphics.blit(BACKGROUND_PARTS, x, i, 0, 0, 6, 4, 1, 16, 16);
        }
    }

}
