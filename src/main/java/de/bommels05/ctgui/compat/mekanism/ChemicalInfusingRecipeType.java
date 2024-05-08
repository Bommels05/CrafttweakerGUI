package de.bommels05.ctgui.compat.mekanism;

import de.bommels05.ctgui.api.SupportedRecipeType;
import de.bommels05.ctgui.api.UnsupportedRecipeException;
import de.bommels05.ctgui.api.UnsupportedViewerException;
import mekanism.api.MekanismAPI;
import mekanism.api.chemical.gas.GasStack;
import mekanism.api.recipes.basic.BasicChemicalInfuserRecipe;
import mekanism.api.recipes.ingredients.creator.IngredientCreatorAccess;
import mekanism.client.recipe_viewer.emi.MekanismEmiRecipeCategory;
import mekanism.client.recipe_viewer.emi.recipe.ChemicalInfuserEmiRecipe;
import mekanism.common.registries.MekanismBlocks;
import mekanism.common.registries.MekanismGases;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.jetbrains.annotations.Nullable;

public class ChemicalInfusingRecipeType extends SupportedRecipeType<BasicChemicalInfuserRecipe> {

    public ChemicalInfusingRecipeType() {
        super(new ResourceLocation(MekanismAPI.MEKANISM_MODID, "chemical_infusing"));

        addAreaScrollAmountEmptyRightClick(22, 10, 18, 60, (r, stack) -> {
            return new BasicChemicalInfuserRecipe(IngredientCreatorAccess.gas().from(stack.getType() == MekanismRecipeUtils.of(r.getLeftInput()).getType() ? stack : new GasStack(stack, MekanismRecipeUtils.getAmount(r.getLeftInput()))), r.getRightInput(), r.getOutputRaw());
        }, r -> {
            return MekanismRecipeUtils.of(r.getLeftInput());
        }, () -> new GasStack(MekanismGases.OXYGEN.get(), 1), MekanismRecipeUtils::limitedChemicalAmountSetter);
        addAreaScrollAmountEmptyRightClick(130, 10, 18, 60, (r, stack) -> {
            return new BasicChemicalInfuserRecipe(r.getLeftInput(), IngredientCreatorAccess.gas().from(stack.getType() == MekanismRecipeUtils.of(r.getRightInput()).getType() ? stack : new GasStack(stack, MekanismRecipeUtils.getAmount(r.getRightInput()))), r.getOutputRaw());
        }, r -> {
            return MekanismRecipeUtils.of(r.getRightInput());
        }, () -> new GasStack(MekanismGases.OXYGEN.get(), 1), MekanismRecipeUtils::limitedChemicalAmountSetter);
        addAreaScrollAmountEmptyRightClick(76, 1, 18, 60, (r, stack) -> {
            return new BasicChemicalInfuserRecipe(r.getLeftInput(), r.getRightInput(), stack.getType() == r.getOutputRaw().getType() ? stack : new GasStack(stack, r.getOutputRaw().getAmount()));
        }, BasicChemicalInfuserRecipe::getOutputRaw, () -> new GasStack(MekanismGases.OXYGEN.get(), 2), MekanismRecipeUtils::limitedChemicalAmountSetter);
    }

    @Override
    public BasicChemicalInfuserRecipe onInitialize(@Nullable BasicChemicalInfuserRecipe recipe) throws UnsupportedRecipeException {
        super.onInitialize(recipe);
        if (recipe == null) {
            return new BasicChemicalInfuserRecipe(IngredientCreatorAccess.gas().from(MekanismGases.OXYGEN, 1),
                    IngredientCreatorAccess.gas().from(MekanismGases.OXYGEN, 1), new GasStack(MekanismGases.OXYGEN.get(), 2));
        }
        return recipe;
    }

    @Override
    public boolean isValid(BasicChemicalInfuserRecipe recipe) {
        return true;
    }

    @Override
    public Object getEmiRecipe(BasicChemicalInfuserRecipe recipe) throws UnsupportedViewerException {
        return new ChemicalInfuserEmiRecipe((MekanismEmiRecipeCategory) getEmiCategory(new ResourceLocation(MekanismAPI.MEKANISM_MODID, "chemical_infusing")), new RecipeHolder<>(nullRl(), recipe));
    }

    @Override
    public String getCraftTweakerString(BasicChemicalInfuserRecipe recipe, String id) {
        return "<recipetype:mekanism:chemical_infusing>.addRecipe(\"" + id + "\", " + MekanismRecipeUtils.getCTString(recipe.getLeftInput()) + ", " + MekanismRecipeUtils.getCTString(recipe.getRightInput()) + ", " + MekanismRecipeUtils.getCTString(recipe.getOutputRaw()) + ");";
    }

    @Override
    public ItemStack getMainOutput(BasicChemicalInfuserRecipe recipe) {
        return new ItemStack(MekanismBlocks.CHEMICAL_INFUSER);
    }
}
