package de.bommels05.ctgui.compat.minecraft;

import de.bommels05.ctgui.api.AmountedIngredient;
import de.bommels05.ctgui.api.SupportedRecipeType;
import de.bommels05.ctgui.api.UnsupportedRecipeException;
import de.bommels05.ctgui.api.UnsupportedViewerException;
import de.bommels05.ctgui.compat.NeoEmiUtils;
import de.bommels05.ctgui.emi.EmiViewerUtils;
import de.bommels05.ctgui.mixin.EmiBrewingRecipeMixin;
import dev.emi.emi.api.recipe.EmiRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public class BrewingRecipeType extends SupportedRecipeType<BrewingRecipe> {

    public BrewingRecipeType() {
        super(new ResourceLocation("minecraft:brewing"));

        addAreaEmptyRightClick(39, 36, 17, 17, (r, am) -> {
            return new BrewingRecipe(am.ensureAmount(1, 1).ingredient(), r.getReagent(), r.getOutput());
        }, r -> {
            return new AmountedIngredient(r.getInput(), 1);
        });
        addAreaEmptyRightClick(62, 2, 17, 17, (r, am) -> {
            return new BrewingRecipe(r.getInput(), am.ensureAmount(1, 1).ingredient(), r.getOutput());
        }, r -> {
            return new AmountedIngredient(r.getReagent(), 1);
        });
        addAreaScrollAmountEmptyRightClick(85, 36, 17, 17, (r, am) -> {
            return new BrewingRecipe(r.getInput(), r.getReagent(), am.asStack());
        }, r -> {
            return AmountedIngredient.of(r.getOutput());
        });
    }

    @Override
    public BrewingRecipe onInitialize(@Nullable BrewingRecipe recipe) throws UnsupportedRecipeException {
        super.onInitialize(recipe);
        if (recipe == null) {
            return new BrewingRecipe(Ingredient.EMPTY, Ingredient.EMPTY, ItemStack.EMPTY);
        }
        return null;
    }

    @Override
    public boolean isValid(BrewingRecipe recipe) {
        return !recipe.getInput().isEmpty() && !recipe.getReagent().isEmpty() && !recipe.getOutput().isEmpty();
    }

    @Override
    public Object getEmiRecipe(BrewingRecipe recipe) throws UnsupportedViewerException {
        //Cursed class loading issues without emi
        return NeoEmiUtils.getEmiRecipe(recipe, nullRl());
    }

    @Override
    public String getCraftTweakerString(BrewingRecipe recipe, String id) {
        ItemStack inputStack = new AmountedIngredient(recipe.getInput(), 1).asStack();
        if (recipe.getOutput().getItem() instanceof PotionItem && inputStack.getItem() instanceof PotionItem) {
            return "brewing.addRecipe(" + getPotionCTString(recipe.getOutput()) + ", " + getCTString(recipe.getReagent()) + ", " + getPotionCTString(inputStack) + ");";
        }
        return "brewing.addRecipe(" + getCTString(recipe.getOutput()) + ", " + getCTString(recipe.getReagent()) + ", " + getCTString(recipe.getInput()) + ");";
    }

    @Override
    public String getCraftTweakerRemoveString(BrewingRecipe recipe, ResourceLocation id) {
        ItemStack inputStack = new AmountedIngredient(recipe.getInput(), 1).asStack();
        if (recipe.getOutput().getItem() instanceof PotionItem && inputStack.getItem() instanceof PotionItem) {
            return "brewing.removeRecipe(" + getPotionCTString(recipe.getOutput()) + ", " + getCTString(recipe.getReagent()) + ", " + getPotionCTString(inputStack) + ");";
        }
        return "brewing.removeRecipe(" + getCTString(recipe.getOutput()) + ", " + getCTString(recipe.getReagent()) + ", " + getCTString(recipe.getInput()) + ");";
    }

    @Override
    public Function<EmiRecipe, BrewingRecipe> getAlternativeEmiRecipeGetter() {
        return recipe -> recipe instanceof EmiBrewingRecipeMixin r ? new BrewingRecipe(EmiViewerUtils.getElseEmpty(r.getInput()), EmiViewerUtils.getElseEmpty(r.getIngredient()), r.getOutput().getItemStack()) : null;
    }
}
