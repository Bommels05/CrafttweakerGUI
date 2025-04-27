package de.bommels05.ctgui.mixin;

import dev.emi.emi.api.recipe.EmiInfoRecipe;
import dev.emi.emi.api.stack.EmiIngredient;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(EmiInfoRecipe.class)
public interface EmiInfoRecipeAccessor {

    @Accessor(value = "stacks", remap = false)
    public List<EmiIngredient> getStacks();

    @Accessor(value = "text", remap = false)
    public List<Component> getText();

}
