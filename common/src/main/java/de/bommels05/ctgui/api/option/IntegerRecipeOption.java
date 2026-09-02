package de.bommels05.ctgui.api.option;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.crafting.Recipe;

public class IntegerRecipeOption<R extends Recipe<?>> extends EditBoxRecipeOption<Integer, R> {

    public IntegerRecipeOption(Component tooltip, int min, int max) {
        super(tooltip, min, max);
    }

    public IntegerRecipeOption(Component tooltip, int min) {
        this(tooltip, min, Integer.MAX_VALUE);
    }

    public IntegerRecipeOption(Component tooltip) {
        this(tooltip, Integer.MIN_VALUE, Integer.MAX_VALUE);
    }

    @Override
    protected Integer parse(String input) {
        if (input.isEmpty()) {
            return min;
        } else {
            return Integer.parseInt(input);
        }
    }
}
