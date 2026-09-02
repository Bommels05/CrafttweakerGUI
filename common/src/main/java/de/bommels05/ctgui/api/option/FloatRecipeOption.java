package de.bommels05.ctgui.api.option;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.crafting.Recipe;

public class FloatRecipeOption<R extends Recipe<?>> extends EditBoxRecipeOption<Float, R> {

    public FloatRecipeOption(Component tooltip, float min, float max) {
        super(tooltip, min, max);
    }

    public FloatRecipeOption(Component tooltip, float min) {
        this(tooltip, min, Float.MAX_VALUE);
    }

    public FloatRecipeOption(Component tooltip) {
        this(tooltip, Float.MIN_VALUE, Float.MAX_VALUE);
    }

    protected Float parse(String input) {
        if (input.isEmpty()) {
            return min;
        } else {
            return Float.parseFloat(input);
        }
    }
}
