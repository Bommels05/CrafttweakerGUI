package de.bommels05.ctgui.compat.mekanism;

import de.bommels05.ctgui.api.SupportedRecipeType;
import de.bommels05.ctgui.api.UnsupportedRecipeException;
import de.bommels05.ctgui.api.UnsupportedViewerException;
import mekanism.api.MekanismAPI;
import mekanism.api.chemical.gas.GasStack;
import mekanism.api.recipes.basic.BasicCentrifugingRecipe;
import mekanism.api.recipes.ingredients.creator.IngredientCreatorAccess;
import mekanism.client.recipe_viewer.emi.MekanismEmiRecipeCategory;
import mekanism.client.recipe_viewer.emi.recipe.GasToGasEmiRecipe;
import mekanism.common.registries.MekanismBlocks;
import mekanism.common.registries.MekanismGases;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.jetbrains.annotations.Nullable;

public class CentrifugingRecipeType extends SupportedRecipeType<BasicCentrifugingRecipe> {

    public CentrifugingRecipeType() {
        super(new ResourceLocation(MekanismAPI.MEKANISM_MODID, "centrifuging"));

        addAreaScrollAmountEmptyRightClick(21, 0, 18, 60, (r, stack) -> {
            return new BasicCentrifugingRecipe(MekanismRecipeUtils.toIngredientKeepAmount(stack, r.getInput()), r.getOutputRaw());
        }, r -> {
            return MekanismRecipeUtils.of(r.getInput());
        }, () -> new ChemicalAmountedIngredient<>(new GasStack(MekanismGases.OXYGEN.get(), 1)), MekanismRecipeUtils::limitedChemicalAmountSetter);
        addAreaScrollAmountEmptyRightClick(129, 0, 18, 60, (r, input) -> {
            GasStack stack = input.toStack();
            return new BasicCentrifugingRecipe(r.getInput(), stack.getType() == r.getOutputRaw().getType() ? stack : new GasStack(stack, r.getOutputRaw().getAmount()));
        }, r -> {
            return new ChemicalAmountedIngredient<>(r.getOutputRaw());
        }, () -> new ChemicalAmountedIngredient<>(new GasStack(MekanismGases.OXYGEN.get(), 1)), MekanismRecipeUtils::limitedChemicalAmountSetter);
    }

    @Override
    public BasicCentrifugingRecipe onInitialize(@Nullable BasicCentrifugingRecipe recipe) throws UnsupportedRecipeException {
        super.onInitialize(recipe);
        if (recipe == null) {
            return new BasicCentrifugingRecipe(IngredientCreatorAccess.gas().from(MekanismGases.OXYGEN, 1), new GasStack(MekanismGases.OXYGEN.get(), 1));
        }
        return recipe;
    }

    @Override
    public boolean isValid(BasicCentrifugingRecipe recipe) {
        return true;
    }

    @Override
    public Object getEmiRecipe(BasicCentrifugingRecipe recipe) throws UnsupportedViewerException {
        return new GasToGasEmiRecipe((MekanismEmiRecipeCategory) getEmiCategory(new ResourceLocation(MekanismAPI.MEKANISM_MODID, "centrifuging")), new RecipeHolder<>(nullRl(), recipe));
    }

    @Override
    public String getCraftTweakerString(BasicCentrifugingRecipe recipe, String id) {
        return "<recipetype:mekanism:centrifuging>.addRecipe(\"" + id + "\", " + MekanismRecipeUtils.getCTString(recipe.getInput()) + ", " + MekanismRecipeUtils.getCTString(recipe.getOutputRaw()) + ");";
    }

    @Override
    public ItemStack getMainOutput(BasicCentrifugingRecipe recipe) {
        return new ItemStack(MekanismBlocks.ISOTOPIC_CENTRIFUGE);
    }
}
