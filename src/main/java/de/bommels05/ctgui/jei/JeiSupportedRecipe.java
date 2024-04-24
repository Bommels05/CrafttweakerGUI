package de.bommels05.ctgui.jei;

import de.bommels05.ctgui.SupportedRecipe;
import de.bommels05.ctgui.api.RecipeTypeManager;
import de.bommels05.ctgui.api.SupportedRecipeType;
import de.bommels05.ctgui.api.UnsupportedViewerException;
import mezz.jei.api.gui.IRecipeLayoutDrawable;
import mezz.jei.api.recipe.IFocus;
import mezz.jei.api.recipe.IRecipeManager;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;

import java.util.List;
import static de.bommels05.ctgui.jei.CTGUIJeiPlugin.RUNTIME;

public class JeiSupportedRecipe<R extends Recipe<?>, T extends SupportedRecipeType<R>> implements SupportedRecipe<R, T> {

    private IRecipeLayoutDrawable<RecipeHolder<R>> recipe;
    private R mcRecipe;
    private final T type;

    @SuppressWarnings("unchecked")
    public JeiSupportedRecipe(IRecipeLayoutDrawable<RecipeHolder<?>> recipe) {
        this.recipe = (IRecipeLayoutDrawable<RecipeHolder<R>>) (IRecipeLayoutDrawable<?>) recipe;
        this.type = (T) RecipeTypeManager.getType(this.recipe.getRecipeCategory().getRecipeType().getUid());
        this.mcRecipe = this.recipe.getRecipe().value();
    }


    @SuppressWarnings("unchecked")
    public JeiSupportedRecipe(ResourceLocation category) {
        type = (T) RecipeTypeManager.getType(category);
    }

    @Override
    public int getWidth() {
        return recipe != null ? recipe.getRect().getWidth() : 0;
    }

    @Override
    public int getHeight() {
        return recipe != null ? recipe.getRect().getHeight() : 0;
    }

    @Override
    public Component getCategoryName() {
        return recipe.getRecipeCategory().getTitle();
    }

    @Override
    public void render(int x, int y, GuiGraphics graphics, int mouseX, int mouseY, Screen screen) {
        recipe.setPosition(x, y);
        recipe.drawRecipe(graphics, mouseX, mouseY);
        recipe.drawOverlays(graphics, mouseX, mouseY);
    }

    @Override
    public void mouseClicked(int x, int y, int mouseX, int mouseY, int button) {}

    @Override
    public T getType() {
        return type;
    }

    @Override
    public R getRecipe() {
        return mcRecipe;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void setRecipe(R recipe) throws UnsupportedViewerException {
        this.mcRecipe = recipe;
        this.recipe = (IRecipeLayoutDrawable<RecipeHolder<R>>) (IRecipeLayoutDrawable<?>) JeiViewerUtils.INSTANCE.getViewerRecipe(type, recipe);
    }
}
