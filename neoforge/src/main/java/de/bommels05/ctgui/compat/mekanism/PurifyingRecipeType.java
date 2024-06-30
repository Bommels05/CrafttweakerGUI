package de.bommels05.ctgui.compat.mekanism;

import de.bommels05.ctgui.api.AmountedIngredient;
import de.bommels05.ctgui.api.SupportedRecipeType;
import de.bommels05.ctgui.api.UnsupportedRecipeException;
import de.bommels05.ctgui.api.UnsupportedViewerException;
import mekanism.api.MekanismAPI;
import mekanism.api.chemical.gas.GasStack;
import mekanism.api.recipes.basic.BasicPurifyingRecipe;
import mekanism.api.recipes.ingredients.creator.IngredientCreatorAccess;
import mekanism.client.recipe_viewer.emi.MekanismEmiRecipeCategory;
import mekanism.client.recipe_viewer.emi.recipe.ItemStackGasToItemStackEmiRecipe;
import mekanism.common.registries.MekanismGases;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.jetbrains.annotations.Nullable;

public class PurifyingRecipeType extends SupportedRecipeType<BasicPurifyingRecipe> {

    public PurifyingRecipeType() {
        super(new ResourceLocation(MekanismAPI.MEKANISM_MODID, "purifying"));

        addAreaScrollAmountEmptyRightClick(36, 1, 17, 17, (r, am) -> {
            return new BasicPurifyingRecipe(MekanismRecipeUtils.of(convertToUnset(am)), r.getChemicalInput(), r.getOutputRaw());
        }, r -> {
            return convertUnset(MekanismRecipeUtils.of(r.getItemInput()));
        });
        addAreaScrollAmountEmptyRightClick(88, 19, 17, 17, (r, am) -> {
            return new BasicPurifyingRecipe(r.getItemInput(), r.getChemicalInput(), convertToUnset(am.asStack()));
        }, r -> {
            return AmountedIngredient.of(convertUnset(r.getOutputRaw()));
        });
        addAreaScrollAmountEmptyRightClick(40, 20, 6, 12, (r, stack) -> {
            return new BasicPurifyingRecipe(r.getItemInput(), MekanismRecipeUtils.toIngredientKeepAmount(stack, r.getChemicalInput()), r.getOutputRaw());
        }, r -> {
            return MekanismRecipeUtils.of(r.getChemicalInput());
        }, () -> new ChemicalAmountedIngredient<>(new GasStack(MekanismGases.OXYGEN.get(), 1)), MekanismRecipeUtils::limitedChemicalAmountSetter);
    }

    @Override
    public BasicPurifyingRecipe onInitialize(@Nullable BasicPurifyingRecipe recipe) throws UnsupportedRecipeException {
        super.onInitialize(recipe);
        if (recipe == null) {
            return new BasicPurifyingRecipe(IngredientCreatorAccess.item().from(UNSET), IngredientCreatorAccess.gas().from(MekanismGases.OXYGEN, 1), UNSET);
        }
        return recipe;
    }

    @Override
    public boolean isValid(BasicPurifyingRecipe recipe) {
        return !recipe.getItemInput().test(UNSET) && !ItemStack.isSameItemSameTags(recipe.getOutputRaw(), UNSET);
    }

    @Override
    public Object getEmiRecipe(BasicPurifyingRecipe recipe) throws UnsupportedViewerException {
        return new ItemStackGasToItemStackEmiRecipe((MekanismEmiRecipeCategory) getEmiCategory(new ResourceLocation(MekanismAPI.MEKANISM_MODID, "purifying")), new RecipeHolder<>(nullRl(), recipe));
    }

    @Override
    public String getCraftTweakerString(BasicPurifyingRecipe recipe, String id) {
        return "<recipetype:mekanism:purifying>.addRecipe(\"" + id + "\", " + getCTString(MekanismRecipeUtils.of(recipe.getItemInput())) + ", " + MekanismRecipeUtils.getCTString(recipe.getChemicalInput()) + ", " + getCTString(recipe.getOutputRaw()) + ");";
    }
}
