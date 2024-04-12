package de.bommels05.ctgui.api.option;

import de.bommels05.ctgui.screen.BetterCheckBox;
import de.bommels05.ctgui.screen.RecipeEditScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.crafting.Recipe;

import java.util.function.BiFunction;
import java.util.function.Consumer;

public class BooleanRecipeOption<R extends Recipe<?>> implements RecipeOption<Boolean, R> {

    private final Component name;
    private BiFunction<R, Boolean, R> listener;
    private BetterCheckBox checkBox;
    private boolean value;

    public BooleanRecipeOption(Component name) {
        this.name = name;
    }

    @Override
    public void addToScreen(RecipeEditScreen<?> screen, int x, int y) {
        checkBox = new BetterCheckBox(x, y, name, screen.getFont(), value, value -> screen.handleRecipeOption(value, listener));
        screen.addRenderableWidget(checkBox);
    }

    @Override
    public void addListener(BiFunction<R, Boolean, R> listener) {
        this.listener = listener;
    }

    @Override
    public int getHeight() {
        return 20;
    }

    @Override
    public Boolean get() {
        if (checkBox == null) {
            return value;
        }
        return checkBox.isSelected();
    }

    @Override
    public void set(Boolean value) {
        if (checkBox == null) {
            this.value = value;
        } else {
            checkBox.setSelected(value);
        }
    }

    @Override
    public void reset() {
        checkBox = null;
        value = false;
    }
}
