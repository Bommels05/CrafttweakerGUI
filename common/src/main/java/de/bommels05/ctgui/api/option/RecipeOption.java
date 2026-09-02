package de.bommels05.ctgui.api.option;

import de.bommels05.ctgui.screen.RecipeEditScreen;
import net.minecraft.world.item.crafting.Recipe;

import java.util.function.BiFunction;

public interface RecipeOption<T, R extends Recipe<?>> {

    public void addToScreen(RecipeEditScreen<R> screen, int x, int y);

    public void addListener(BiFunction<R, T, R> listener);

    public int getHeight();

    public T get();

    public void set(T value);

    public void reset();

}
