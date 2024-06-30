package de.bommels05.ctgui.jei;

import com.mojang.datafixers.util.Either;
import de.bommels05.ctgui.SupportedRecipe;
import de.bommels05.ctgui.api.RecipeTypeManager;
import de.bommels05.ctgui.api.SupportedRecipeType;
import de.bommels05.ctgui.api.UnsupportedViewerException;
import mezz.jei.api.gui.IRecipeLayoutDrawable;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;

public class JeiSupportedRecipe<R extends Recipe<?>, T extends SupportedRecipeType<R>> implements SupportedRecipe<R, T> {

    private Either<IRecipeLayoutDrawable<RecipeHolder<R>>, IRecipeLayoutDrawable<R>> recipe;
    private R mcRecipe;
    private final T type;

    @SuppressWarnings("unchecked")
    public JeiSupportedRecipe(Either<IRecipeLayoutDrawable<RecipeHolder<R>>, IRecipeLayoutDrawable<R>> recipe) {
        this.recipe = recipe;
        this.type = (T) RecipeTypeManager.getType(getUnknown().getRecipeCategory().getRecipeType().getUid());
        this.mcRecipe = this.recipe.map(r -> r.getRecipe().value(), IRecipeLayoutDrawable::getRecipe);
    }


    @SuppressWarnings("unchecked")
    public JeiSupportedRecipe(ResourceLocation category) {
        type = (T) RecipeTypeManager.getType(category);
    }

    @Override
    public int getWidth() {
        return recipe != null ? getUnknown().getRect().getWidth() : 0;
    }

    @Override
    public int getHeight() {
        return recipe != null ? getUnknown().getRect().getHeight() : 0;
    }

    @Override
    public Component getCategoryName() {
        return getUnknown().getRecipeCategory().getTitle();
    }

    @Override
    public void render(int x, int y, GuiGraphics graphics, int mouseX, int mouseY, Screen screen) {
        getUnknown().setPosition(x, y);
        getUnknown().drawRecipe(graphics, mouseX, mouseY);
        getUnknown().drawOverlays(graphics, mouseX, mouseY);
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
        this.recipe = (Either<IRecipeLayoutDrawable<RecipeHolder<R>>, IRecipeLayoutDrawable<R>>) (Either<?, ?>) JeiViewerUtils.INSTANCE.getViewerRecipe(type, recipe);
    }

    private IRecipeLayoutDrawable<?> getUnknown() {
        return recipe.map(r -> r, r -> r);
    }
}
