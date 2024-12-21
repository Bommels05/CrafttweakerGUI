package de.bommels05.ctgui.mixin;

import mezz.jei.gui.recipes.RecipeGuiLayouts;
import mezz.jei.gui.recipes.RecipeLayoutWithButtons;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(RecipeGuiLayouts.class)
public interface RecipeGuiLayoutsAccessor {

    @Accessor(remap = false)
    public List<RecipeLayoutWithButtons<?>> getRecipeLayoutsWithButtons();

}
