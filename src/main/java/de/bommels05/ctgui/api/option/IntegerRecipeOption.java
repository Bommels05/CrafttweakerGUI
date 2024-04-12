package de.bommels05.ctgui.api.option;

import de.bommels05.ctgui.screen.RecipeEditScreen;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.crafting.Recipe;

import java.util.function.BiFunction;

public class IntegerRecipeOption<R extends Recipe<?>> implements RecipeOption<Integer, R> {

    private EditBox editBox;
    private BiFunction<R, Integer, R> listener;
    private final Component tooltip;
    private final int min;
    private final int max;
    private int value;

    public IntegerRecipeOption(Component tooltip, int min, int max) {
        this.tooltip = tooltip;
        this.min = min;
        this.max = max;
    }

    public IntegerRecipeOption(Component tooltip, int min) {
        this(tooltip, min, Integer.MAX_VALUE);
    }

    public IntegerRecipeOption(Component tooltip) {
        this(tooltip, Integer.MIN_VALUE, Integer.MAX_VALUE);
    }

    @Override
    public void addToScreen(RecipeEditScreen<?> screen, int x, int y) {
        editBox = new EditBox(screen.getFont(), x, y, 100, 18, Component.empty());
        editBox.setMaxLength(256);
        editBox.setTooltip(Tooltip.create(tooltip));
        editBox.setValue(String.valueOf(value));
        editBox.setResponder(value -> screen.handleRecipeOption(parse(value), listener));
        editBox.setFilter(value -> {
            try {
                int i = Integer.parseInt(value);
                if (i < min) {
                    editBox.setValue(String.valueOf(min));
                    return false;
                }
                if (i > max) {
                    editBox.setValue(String.valueOf(max));
                    return false;
                }
                return true;
            } catch (NumberFormatException e) {
                return value.isEmpty();
            }
        });
        screen.addRenderableWidget(editBox);
    }

    @Override
    public void addListener(BiFunction<R, Integer, R> listener) {
        this.listener = listener;
    }

    @Override
    public int getHeight() {
        return 18;
    }

    @Override
    public Integer get() {
        if (editBox == null) {
            return value;
        }
        return parse(editBox.getValue());
    }

    @Override
    public void set(Integer value) {
        if (editBox == null) {
            this.value = value;
        } else {
            editBox.setValue(String.valueOf(value));
        }
    }

    @Override
    public void reset() {
        editBox = null;
        value = min;
    }

    private int parse(String input) {
        if (input.isEmpty()) {
            return min;
        } else {
            return Integer.parseInt(input);
        }
    }
}
