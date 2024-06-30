package de.bommels05.ctgui.api.option;

import de.bommels05.ctgui.screen.RecipeEditScreen;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.crafting.Recipe;

import java.util.function.BiFunction;

public class DoubleRecipeOption<R extends Recipe<?>> implements RecipeOption<Double, R> {
    private EditBox editBox;
    private BiFunction<R, Double, R> listener;
    private final Component tooltip;
    private final double min;
    private final double max;
    private double value;

    public DoubleRecipeOption(Component tooltip, double min, double max) {
        this.tooltip = tooltip;
        this.min = min;
        this.max = max;
    }

    public DoubleRecipeOption(Component tooltip, double min) {
        this(tooltip, min, Double.MAX_VALUE);
    }

    public DoubleRecipeOption(Component tooltip) {
        this(tooltip, Double.MIN_VALUE, Double.MAX_VALUE);
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
                double d = Double.parseDouble(value);
                if (d < min) {
                    editBox.setValue(String.valueOf(min));
                    return false;
                }
                if (d > max) {
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
    public void addListener(BiFunction<R, Double, R> listener) {
        this.listener = listener;
    }

    @Override
    public int getHeight() {
        return 18;
    }

    @Override
    public Double get() {
        if (editBox == null) {
            return value;
        }
        return parse(editBox.getValue());
    }

    @Override
    public void set(Double value) {
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

    private double parse(String input) {
        if (input.isEmpty()) {
            return min;
        } else {
            return Double.parseDouble(input);
        }
    }
}
