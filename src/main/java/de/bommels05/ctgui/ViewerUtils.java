package de.bommels05.ctgui;

import net.minecraft.world.item.crafting.Recipe;

public interface ViewerUtils {

    public <T extends Recipe<?>> void inject(ChangedRecipeManager.ChangedRecipe<T> recipe);

    public <T extends Recipe<?>> void unInject(ChangedRecipeManager.ChangedRecipe<T> recipe);

}
