package de.bommels05.ctgui.compat.mekanism;

import de.bommels05.ctgui.api.FluidAmountedIngredient;
import de.bommels05.ctgui.api.SupportedRecipeType;
import de.bommels05.ctgui.api.UnsupportedRecipeException;
import de.bommels05.ctgui.api.UnsupportedViewerException;
import de.bommels05.ctgui.api.option.DoubleRecipeOption;
import mekanism.api.MekanismAPI;
import mekanism.api.chemical.gas.GasStack;
import mekanism.api.math.FloatingLong;
import mekanism.api.recipes.basic.BasicElectrolysisRecipe;
import mekanism.api.recipes.ingredients.creator.IngredientCreatorAccess;
import mekanism.client.recipe_viewer.emi.MekanismEmiRecipeCategory;
import mekanism.client.recipe_viewer.emi.recipe.ElectrolysisEmiRecipe;
import mekanism.common.registries.MekanismBlocks;
import mekanism.common.registries.MekanismGases;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;

public class SeperatingRecipeType extends SupportedRecipeType<BasicElectrolysisRecipe> {

    private final DoubleRecipeOption<BasicElectrolysisRecipe> energyMultiplier = new DoubleRecipeOption<>(Component.translatable("ctgui.editing.options.energy_multiplier"), 1);

    public SeperatingRecipeType() {
        super(new ResourceLocation(MekanismAPI.MEKANISM_MODID, "separating"));

        addAreaScrollAmountEmptyRightClick(1, 1, 18, 60, (r, stack) -> {
            return new BasicElectrolysisRecipe(MekanismRecipeUtils.toIngredientKeepAmount(stack, r.getInput()), r.getEnergyMultiplier(), r.getLeftGasOutput(), r.getRightGasOutput());
        }, r -> {
            return MekanismRecipeUtils.of(r.getInput());
        }, () -> new FluidAmountedIngredient(new FluidStack(Fluids.WATER, 10)), SupportedRecipeType::limitedFluidAmountSetter);
        addAreaScrollAmountEmptyRightClick(54, 9, 18, 30, (r, input) -> {
            GasStack stack = input.toStack();
            return new BasicElectrolysisRecipe(r.getInput(), r.getEnergyMultiplier(), stack.getType() == r.getLeftGasOutput().getType() ? stack : new GasStack(stack, r.getLeftGasOutput().getAmount()), r.getRightGasOutput());
        }, r -> {
            return new ChemicalAmountedIngredient<>(r.getLeftGasOutput());
        }, () -> new ChemicalAmountedIngredient<>(new GasStack(MekanismGases.OXYGEN.get(), 10)), MekanismRecipeUtils::limitedChemicalAmountSetter);
        addAreaScrollAmountEmptyRightClick(96, 9, 18, 30, (r, input) -> {
            GasStack stack = input.toStack();
            return new BasicElectrolysisRecipe(r.getInput(), r.getEnergyMultiplier(), r.getLeftGasOutput(), stack.getType() == r.getRightGasOutput().getType() ? stack : new GasStack(stack, r.getRightGasOutput().getAmount()));
        }, r -> {
            return new ChemicalAmountedIngredient<>(r.getRightGasOutput());
        }, () -> new ChemicalAmountedIngredient<>(new GasStack(MekanismGases.OXYGEN.get(), 10)), MekanismRecipeUtils::limitedChemicalAmountSetter);

        addOption(energyMultiplier, (r, energyMultiplier) -> {
            return new BasicElectrolysisRecipe(r.getInput(), FloatingLong.create(energyMultiplier), r.getLeftGasOutput(), r.getRightGasOutput());
        });
    }

    @Override
    public BasicElectrolysisRecipe onInitialize(@Nullable BasicElectrolysisRecipe recipe) throws UnsupportedRecipeException {
        super.onInitialize(recipe);
        if (recipe == null) {
            return new BasicElectrolysisRecipe(IngredientCreatorAccess.fluid().from(Fluids.WATER, 10), FloatingLong.create(1),
                    new GasStack(MekanismGases.OXYGEN.get(), 10), new GasStack(MekanismGases.OXYGEN.get(), 10));
        }
        energyMultiplier.set(recipe.getEnergyMultiplier().doubleValue());
        return recipe;
    }

    @Override
    public boolean isValid(BasicElectrolysisRecipe recipe) {
        return true;
    }

    @Override
    public Object getEmiRecipe(BasicElectrolysisRecipe recipe) throws UnsupportedViewerException {
        return new ElectrolysisEmiRecipe((MekanismEmiRecipeCategory) getEmiCategory(new ResourceLocation(MekanismAPI.MEKANISM_MODID, "separating")), new RecipeHolder<>(nullRl(), recipe));
    }

    @Override
    public String getCraftTweakerString(BasicElectrolysisRecipe recipe, String id) {
        return "<recipetype:mekanism:separating>.addRecipe(\"" + id + "\", " + getCTString(MekanismRecipeUtils.of(recipe.getInput())) + ", " + MekanismRecipeUtils.getCTString(recipe.getLeftGasOutput()) + ", " + MekanismRecipeUtils.getCTString(recipe.getRightGasOutput()) + ", " + recipe.getEnergyMultiplier() + ");";
    }

    @Override
    public ItemStack getMainOutput(BasicElectrolysisRecipe recipe) {
        return new ItemStack(MekanismBlocks.ELECTROLYTIC_SEPARATOR);
    }
}
