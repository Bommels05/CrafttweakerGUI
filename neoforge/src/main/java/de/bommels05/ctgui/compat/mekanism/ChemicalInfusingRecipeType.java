package de.bommels05.ctgui.compat.mekanism;

import de.bommels05.ctgui.CraftTweakerGUI;
import de.bommels05.ctgui.api.SupportedRecipeType;
import de.bommels05.ctgui.api.UnsupportedRecipeException;
import de.bommels05.ctgui.api.UnsupportedViewerException;
import mekanism.api.MekanismAPI;
import mekanism.api.chemical.ChemicalStack;
import mekanism.api.recipes.basic.BasicChemicalInfuserRecipe;
import mekanism.api.recipes.ingredients.creator.IngredientCreatorAccess;
import mekanism.client.recipe_viewer.emi.MekanismEmiRecipeCategory;
import mekanism.client.recipe_viewer.emi.recipe.ChemicalChemicalToChemicalEmiRecipe;
import mekanism.common.registries.MekanismBlocks;
import mekanism.common.registries.MekanismChemicals;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.jetbrains.annotations.Nullable;

public class ChemicalInfusingRecipeType extends SupportedRecipeType<BasicChemicalInfuserRecipe> {

    public ChemicalInfusingRecipeType() {
        super(CraftTweakerGUI.rl(MekanismAPI.MEKANISM_MODID, "chemical_infusing"));

        addAreaScrollAmountEmptyRightClick(22, 10, 18, 60, (r, stack) -> {
            return new BasicChemicalInfuserRecipe(MekanismRecipeUtils.toIngredientKeepAmount(stack, r.getLeftInput()), r.getRightInput(), r.getOutputRaw());
        }, r -> {
            return MekanismRecipeUtils.of(r.getLeftInput());
        }, () -> new ChemicalAmountedIngredient(new ChemicalStack(MekanismChemicals.OXYGEN.get(), 1)), MekanismRecipeUtils::limitedChemicalAmountSetter);
        addAreaScrollAmountEmptyRightClick(130, 10, 18, 60, (r, stack) -> {
            return new BasicChemicalInfuserRecipe(r.getLeftInput(), MekanismRecipeUtils.toIngredientKeepAmount(stack, r.getRightInput()), r.getOutputRaw());
        }, r -> {
            return MekanismRecipeUtils.of(r.getRightInput());
        }, () -> new ChemicalAmountedIngredient(new ChemicalStack(MekanismChemicals.OXYGEN.get(), 1)), MekanismRecipeUtils::limitedChemicalAmountSetter);
        addAreaScrollAmountEmptyRightClick(76, 1, 18, 60, (r, input) -> {
            ChemicalStack stack = input.toStack();
            return new BasicChemicalInfuserRecipe(r.getLeftInput(), r.getRightInput(), stack.getChemical() == r.getOutputRaw().getChemical() ? stack : new ChemicalStack(stack.getChemical(), r.getOutputRaw().getAmount()));
        }, r -> {
            return new ChemicalAmountedIngredient(r.getOutputRaw());
        }, () -> new ChemicalAmountedIngredient(new ChemicalStack(MekanismChemicals.OXYGEN.get(), 2)), MekanismRecipeUtils::limitedChemicalAmountSetter);
    }

    @Override
    public BasicChemicalInfuserRecipe onInitialize(@Nullable BasicChemicalInfuserRecipe recipe) throws UnsupportedRecipeException {
        super.onInitialize(recipe);
        if (recipe == null) {
            return new BasicChemicalInfuserRecipe(IngredientCreatorAccess.chemicalStack().from(MekanismChemicals.OXYGEN, 1),
                    IngredientCreatorAccess.chemicalStack().from(MekanismChemicals.OXYGEN, 1), new ChemicalStack(MekanismChemicals.OXYGEN.get(), 2));
        }
        return recipe;
    }

    @Override
    public boolean isValid(BasicChemicalInfuserRecipe recipe) {
        return true;
    }

    @Override
    public Object getEmiRecipe(BasicChemicalInfuserRecipe recipe) throws UnsupportedViewerException {
        return new ChemicalChemicalToChemicalEmiRecipe((MekanismEmiRecipeCategory) getEmiCategory(CraftTweakerGUI.rl(MekanismAPI.MEKANISM_MODID, "chemical_infusing")), new RecipeHolder<>(nullRl(), recipe));
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
