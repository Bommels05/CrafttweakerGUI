package de.bommels05.ctgui.mixin;

import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.recipe.EmiBrewingRecipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(EmiBrewingRecipe.class)
public interface EmiBrewingRecipeMixin {

    @Accessor(value = "input")
    public EmiIngredient getInput();

    @Accessor(value = "ingredient")
    public EmiIngredient getIngredient();

    @Accessor(value = "output")
    public EmiStack getOutput();

}
