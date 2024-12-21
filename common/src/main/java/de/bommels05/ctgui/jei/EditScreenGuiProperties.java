package de.bommels05.ctgui.jei;


import de.bommels05.ctgui.screen.RecipeEditScreen;
import mezz.jei.api.gui.handlers.IGuiProperties;
import net.minecraft.client.gui.screens.Screen;

public class EditScreenGuiProperties implements IGuiProperties {

    private final int minX;
    private final int minY;
    private final int xSize;
    private final int ySize;
    private final int width;
    private final int height;

    public EditScreenGuiProperties(RecipeEditScreen<?> screen) {
        this.minX = screen.getMinX();
        this.minY = screen.getMinY();
        this.xSize = screen.getMaxX() - screen.getMinX();
        this.ySize = screen.getMaxY() - screen.getMinY();
        this.width = screen.width;
        this.height = screen.height;
    }

    @Override
    public Class<? extends Screen> screenClass() {
        return RecipeEditScreen.class;
    }

    @Override
    public int guiLeft() {
        return minX;
    }

    @Override public int guiTop() {
        return minY;
    }

    @Override
    public int guiXSize() {
        return xSize;
    }

    @Override
    public int guiYSize() {
        return ySize;
    }

    @Override
    public int screenWidth() {
        return width;
    }

    @Override
    public int screenHeight() {
        return height;
    }
}
