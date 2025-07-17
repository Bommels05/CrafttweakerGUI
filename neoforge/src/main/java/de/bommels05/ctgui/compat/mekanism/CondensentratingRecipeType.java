package de.bommels05.ctgui.compat.mekanism;

import de.bommels05.ctgui.CraftTweakerGUI;
import de.bommels05.ctgui.NeoLoaderUtils;
import de.bommels05.ctgui.api.FluidAmountedIngredient;
import de.bommels05.ctgui.api.SupportedRecipeType;
import de.bommels05.ctgui.api.UnsupportedRecipeException;
import de.bommels05.ctgui.api.UnsupportedViewerException;
import mekanism.api.MekanismAPI;
import mekanism.api.chemical.ChemicalStack;
import mekanism.api.recipes.basic.BasicRotaryRecipe;
import mekanism.api.recipes.ingredients.creator.IngredientCreatorAccess;
import mekanism.client.recipe_viewer.emi.MekanismEmiRecipeCategory;
import mekanism.client.recipe_viewer.emi.recipe.RotaryEmiRecipe;
import mekanism.common.registries.MekanismBlocks;
import mekanism.common.registries.MekanismChemicals;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;

public class CondensentratingRecipeType extends SupportedRecipeType<BasicRotaryRecipe> {

    public CondensentratingRecipeType() {
        super(CraftTweakerGUI.rl(MekanismAPI.MEKANISM_MODID, "condensentrating"));

        addAreaScrollAmountEmptyRightClick(22, 1, 18, 60, (r, stack) -> {
            return new BasicRotaryRecipe(MekanismRecipeUtils.toIngredientKeepAmount(stack, r.getChemicalInput()), r.getFluidOutputRaw());
        }, r -> {
            return MekanismRecipeUtils.of(r.getChemicalInput());
        }, () -> new ChemicalAmountedIngredient(new ChemicalStack(MekanismChemicals.OXYGEN.get(), 1)), MekanismRecipeUtils::limitedChemicalAmountSetter);
        addAreaScrollAmountEmptyRightClick(130, 1, 18, 60, (r, input) -> {
            FluidStack stack = input.toStack();
            return new BasicRotaryRecipe(r.getChemicalInput(), stack.getFluid() == r.getFluidOutputRaw().getFluid() ? stack : stack.copyWithAmount(r.getFluidOutputRaw().getAmount()));
        }, r -> {
            return new FluidAmountedIngredient(r.getFluidOutputRaw());
        }, () -> new FluidAmountedIngredient(new FluidStack(Fluids.WATER, 1)), NeoLoaderUtils::limitedFluidAmountSetter);
    }

    @Override
    public BasicRotaryRecipe onInitialize(@Nullable BasicRotaryRecipe recipe) throws UnsupportedRecipeException {
        super.onInitialize(recipe);
        if (recipe == null) {
            return new BasicRotaryRecipe(IngredientCreatorAccess.chemicalStack().from(MekanismChemicals.OXYGEN, 1), new FluidStack(Fluids.WATER, 1));
        }
        return recipe;
    }

    @Override
    public boolean isValid(BasicRotaryRecipe recipe) {
        return true;
    }

    @Override
    public Object getEmiRecipe(BasicRotaryRecipe recipe) throws UnsupportedViewerException {
        return new RotaryEmiRecipe((MekanismEmiRecipeCategory) getEmiCategory(CraftTweakerGUI.rl(MekanismAPI.MEKANISM_MODID, "condensentrating")), nullRl(), new RecipeHolder<>(nullRl(), recipe), true);
    }

    @Override
    public String getCraftTweakerString(BasicRotaryRecipe recipe, String id) {
        return "<recipetype:mekanism:rotary>.addRecipe(\"" + id + "\", " + MekanismRecipeUtils.getCTString(recipe.getChemicalInput()) + ", " + NeoLoaderUtils.getCTString(recipe.getFluidOutputRaw()) + ");";
    }

    @Override
    public ItemStack getMainOutput(BasicRotaryRecipe recipe) {
        return new ItemStack(MekanismBlocks.ROTARY_CONDENSENTRATOR);
    }
}
