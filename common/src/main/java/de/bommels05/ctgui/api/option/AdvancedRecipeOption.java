package de.bommels05.ctgui.api.option;

import net.minecraft.world.item.crafting.Recipe;

import java.util.function.BiPredicate;

public interface AdvancedRecipeOption<T, R extends Recipe<?>> extends RecipeOption<T, R> {

    public void addAdvancedFilter(BiPredicate<R, T> advancedFilter);

}
