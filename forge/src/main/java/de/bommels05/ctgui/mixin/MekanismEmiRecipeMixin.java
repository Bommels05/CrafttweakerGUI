package de.bommels05.ctgui.mixin;

/*import de.bommels05.ctgui.Config;
import de.bommels05.ctgui.api.AmountedIngredient;
import de.bommels05.ctgui.screen.RecipeEditScreen;
import dev.emi.emi.api.stack.EmiIngredient;
import mekanism.api.recipes.ingredients.ItemStackIngredient;
import mekanism.client.recipe_viewer.emi.recipe.MekanismEmiRecipe;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.crafting.Ingredient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MekanismEmiRecipe.class)
public class MekanismEmiRecipeMixin {

    @Inject(method = "ingredient(Lmekanism/api/recipes/ingredients/ItemStackIngredient;)Ldev/emi/emi/api/stack/EmiIngredient;", at = @At(value = "HEAD"), cancellable = true, remap = false)
    protected void alwaysDisplayTag(ItemStackIngredient ingredient, CallbackInfoReturnable<EmiIngredient> cir) {
        if (Config.noTagCollapsing && (Minecraft.getInstance().screen instanceof RecipeEditScreen<?> || Config.showTagsEverywhere) && new AmountedIngredient(ingredient.ingredient().ingredient(), 1).isTag()) {
            Ingredient i = ingredient.ingredient().ingredient();
            cir.setReturnValue(EmiIngredient.of(((Ingredient.TagValue) i.getValues()[0]).tag(), ingredient.ingredient().count()));
        }
    }

}*/
