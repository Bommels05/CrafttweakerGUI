package de.bommels05.ctgui.jei;

import de.bommels05.ctgui.api.AmountedIngredient;
import de.bommels05.ctgui.api.SupportedRecipeType;
import de.bommels05.ctgui.screen.RecipeEditScreen;
import mezz.jei.api.gui.handlers.IGhostIngredientHandler;
import mezz.jei.api.ingredients.ITypedIngredient;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;

import java.util.ArrayList;
import java.util.List;

public class JeiDragAndDropHandler implements IGhostIngredientHandler<RecipeEditScreen> {

    @Override
    @SuppressWarnings("unchecked")
    public <I> List<Target<I>> getTargetsTyped(RecipeEditScreen screen, ITypedIngredient<I> ingredient, boolean doStart) {
        List<Target<I>> targets = new ArrayList<>();
        for (SupportedRecipeType.Area<?, ?, ?> area : ((RecipeEditScreen<Recipe<?>>) screen).getRecipeType().getAreas()) {
            targets.add(new Target<I>() {
                @Override
                public Rect2i getArea() {
                    return new Rect2i(screen.getRecipeX() + area.x(), screen.getRecipeY() + area.y(), area.width(), area.height());
                }

                @Override
                public void accept(I ingredient) {
                    if (ingredient instanceof ItemStack stack) {
                        screen.handleDragAndDrop(screen.getRecipeX() + area.x(), screen.getRecipeY() + area.y(), AmountedIngredient.of(stack));
                    } else {
                        screen.handleDragAndDropSpecial(screen.getRecipeX() + area.x(), screen.getRecipeY() + area.y(), ingredient);
                    }
                }
            });
        }
        return targets;
    }

    @Override
    public void onComplete() {}

    @Override
    public boolean shouldHighlightTargets() {
        return false;
    }
}
