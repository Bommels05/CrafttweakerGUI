package de.bommels05.ctgui.api.option;

import de.bommels05.ctgui.screen.RecipeEditScreen;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.crafting.Recipe;

import java.util.function.BiFunction;

public class FloatRecipeOption<R extends Recipe<?>> implements RecipeOption<Float, R> {
    private EditBox editBox;
    private BiFunction<R, Float, R> listener;
    private final Component tooltip;
    private final float min;
    private final float max;
    private float value;

    public FloatRecipeOption(Component tooltip, float min, float max) {
        this.tooltip = tooltip;
        this.min = min;
        this.max = max;
    }

    public FloatRecipeOption(Component tooltip, float min) {
        this(tooltip, min, Float.MAX_VALUE);
    }

    public FloatRecipeOption(Component tooltip) {
        this(tooltip, Float.MIN_VALUE, Float.MAX_VALUE);
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
                float f = Float.parseFloat(value);
                if (f < min) {
                    editBox.setValue(String.valueOf(min));
                    return false;
                }
                if (f > max) {
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
    public void addListener(BiFunction<R, Float, R> listener) {
        this.listener = listener;
    }

    @Override
    public int getHeight() {
        return 18;
    }

    @Override
    public Float get() {
        if (editBox == null) {
            return value;
        }
        return parse(editBox.getValue());
    }

    @Override
    public void set(Float value) {
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

    private float parse(String input) {
        if (input.isEmpty()) {
            return min;
        } else {
            return Float.parseFloat(input);
        }
    }
}
