package de.bommels05.ctgui;

import de.bommels05.ctgui.api.SupportedRecipeType;
import de.bommels05.ctgui.api.UnsupportedViewerException;
import de.bommels05.ctgui.screen.RecipeEditScreen;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.crafting.Recipe;

public interface SupportedRecipe<R extends Recipe<?>, T extends SupportedRecipeType<R>> {

    public int getWidth();

    public int getHeight();

    public Component getCategoryName();

    public void render(int x, int y, GuiGraphics graphics, int mouseX, int mouseY, Screen screen);

    public void mouseClicked(int x, int y, int mouseX, int mouseY, int button);

    public T getType();

    public R getRecipe();

    public void setRecipe(R recipe) throws UnsupportedViewerException;
}
