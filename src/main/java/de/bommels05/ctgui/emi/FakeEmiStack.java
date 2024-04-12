package de.bommels05.ctgui.emi;

import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public class FakeEmiStack extends EmiStack {

    private final EmiIngredient ingredient;

    public FakeEmiStack(EmiIngredient ingredient) {
        this.ingredient = ingredient;
    }

    @Override
    public List<EmiStack> getEmiStacks() {
        return ingredient.getEmiStacks();
    }

    @Override
    public EmiStack copy() {
        return new FakeEmiStack(ingredient.copy());
    }

    @Override
    public boolean isEmpty() {
        return ingredient.isEmpty();
    }

    @Override
    public long getAmount() {
        return ingredient.getAmount();
    }

    @Override
    public EmiStack setAmount(long amount) {
        return new FakeEmiStack(ingredient.setAmount(amount));
    }

    @Override
    public float getChance() {
        return ingredient.getChance();
    }

    @Override
    public EmiStack setChance(float chance) {
        return new FakeEmiStack(ingredient.setChance(chance));
    }

    @Override
    public CompoundTag getNbt() {
        return new CompoundTag();
    }

    @Override
    public Object getKey() {
        return ingredient;
    }

    @Override
    public ResourceLocation getId() {
        return ingredient.getEmiStacks().get(0).getId();
    }

    @Override
    public List<Component> getTooltipText() {
        return ingredient.getEmiStacks().get(0).getTooltipText();
    }

    @Override
    public void render(GuiGraphics draw, int x, int y, float delta) {
        ingredient.render(draw, x, y, delta);
    }

    @Override
    public void render(GuiGraphics draw, int x, int y, float delta, int flags) {
        ingredient.render(draw, x, y, delta, flags);
    }

    @Override
    public List<ClientTooltipComponent> getTooltip() {
        return ingredient.getTooltip();
    }

    @Override
    public Component getName() {
        return ingredient.getEmiStacks().get(0).getName();
    }


}
