package de.bommels05.ctgui.api.option;

import de.bommels05.ctgui.screen.RecipeEditScreen;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.crafting.Recipe;

import java.util.function.BiFunction;

public class RangedRecipeOption<R extends Recipe<?>> implements RecipeOption<Integer, R> {

    private Slider slider;
    private BiFunction<R, Integer, R> listener;
    private final Component name;
    private final int min;
    private final int max;
    private int value;

    public RangedRecipeOption(Component name, int min, int max) {
        this.name = name;
        this.min = min;
        this.max = max;
    }

    @Override
    public void addToScreen(RecipeEditScreen<R> screen, int x, int y) {
        slider = new Slider(screen, x, y);
        screen.addRenderableWidget(slider);
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
        if (slider == null) {
            return value;
        }
        return slider.getValue();
    }

    @Override
    public void set(Integer value) {
        if (slider == null) {
            this.value = value;
        } else {
            slider.setValue(value);
        }
    }

    @Override
    public void reset() {
        slider = null;
        value = min;
    }

    private class Slider extends AbstractSliderButton {
        private final RecipeEditScreen<R> screen;

        public Slider(RecipeEditScreen<R> screen, int x, int y) {
            super(x, y, 100, 18, Component.empty(), 0);
            this.screen = screen;
            setValue(RangedRecipeOption.this.value);
            updateMessage();
        }

        @Override
        protected void updateMessage() {
            setMessage(MutableComponent.create(name.getContents()).append(": " + getValue()));
        }

        @Override
        protected void applyValue() {
            screen.handleRecipeOption(getValue(), listener);
        }

        public void setValue(int value) {
            this.value = (value - min) / getStep();
        }

        public int getValue() {
            return (int) getRealValue();
        }

        private double getRealValue() {
            return value / getStep() + min;
        }

        private double getStep() {
            return 1d / (max - min);
        }
    }
}
