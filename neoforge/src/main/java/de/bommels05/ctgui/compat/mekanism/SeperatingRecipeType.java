package de.bommels05.ctgui.compat.mekanism;

import de.bommels05.ctgui.CraftTweakerGUI;
import de.bommels05.ctgui.NeoLoaderUtils;
import de.bommels05.ctgui.api.FluidAmountedIngredient;
import de.bommels05.ctgui.api.SupportedRecipeType;
import de.bommels05.ctgui.api.UnsupportedRecipeException;
import de.bommels05.ctgui.api.UnsupportedViewerException;
import de.bommels05.ctgui.api.option.LongRecipeOption;
import mekanism.api.MekanismAPI;
import mekanism.api.chemical.ChemicalStack;
import mekanism.api.recipes.basic.BasicElectrolysisRecipe;
import mekanism.api.recipes.ingredients.creator.IngredientCreatorAccess;
import mekanism.client.recipe_viewer.emi.MekanismEmiRecipeCategory;
import mekanism.client.recipe_viewer.emi.recipe.ElectrolysisEmiRecipe;
import mekanism.common.registries.MekanismBlocks;
import mekanism.common.registries.MekanismChemicals;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;

public class SeperatingRecipeType extends SupportedRecipeType<BasicElectrolysisRecipe> {

    private final LongRecipeOption<BasicElectrolysisRecipe> energyMultiplier = new LongRecipeOption<>(Component.translatable("ctgui.editing.options.energy_multiplier"), 1);

    public SeperatingRecipeType() {
        super(CraftTweakerGUI.rl(MekanismAPI.MEKANISM_MODID, "separating"));

        addAreaScrollAmountEmptyRightClick(1, 1, 18, 60, (r, stack) -> {
            return new BasicElectrolysisRecipe(MekanismRecipeUtils.toIngredientKeepAmount(stack, r.getInput()), r.getEnergyMultiplier(), r.getLeftChemicalOutput(), r.getRightChemicalOutput());
        }, r -> {
            return MekanismRecipeUtils.of(r.getInput());
        }, () -> new FluidAmountedIngredient(new FluidStack(Fluids.WATER, 10)), NeoLoaderUtils::limitedFluidAmountSetter);
        addAreaScrollAmountEmptyRightClick(54, 9, 18, 30, (r, input) -> {
            ChemicalStack stack = input.toStack();
            return new BasicElectrolysisRecipe(r.getInput(), r.getEnergyMultiplier(), stack.getChemical() == r.getLeftChemicalOutput().getChemical() ? stack : stack.copyWithAmount(r.getLeftChemicalOutput().getAmount()), r.getRightChemicalOutput());
        }, r -> {
            return new ChemicalAmountedIngredient(r.getLeftChemicalOutput());
        }, () -> new ChemicalAmountedIngredient(new ChemicalStack(MekanismChemicals.OXYGEN, 10)), MekanismRecipeUtils::limitedChemicalAmountSetter);
        addAreaScrollAmountEmptyRightClick(96, 9, 18, 30, (r, input) -> {
            ChemicalStack stack = input.toStack();
            return new BasicElectrolysisRecipe(r.getInput(), r.getEnergyMultiplier(), r.getLeftChemicalOutput(), stack.getChemical() == r.getRightChemicalOutput().getChemical() ? stack : stack.copyWithAmount(r.getRightChemicalOutput().getAmount()));
        }, r -> {
            return new ChemicalAmountedIngredient(r.getRightChemicalOutput());
        }, () -> new ChemicalAmountedIngredient(new ChemicalStack(MekanismChemicals.OXYGEN, 10)), MekanismRecipeUtils::limitedChemicalAmountSetter);

        addOption(energyMultiplier, (r, energyMultiplier) -> {
            return new BasicElectrolysisRecipe(r.getInput(), energyMultiplier, r.getLeftChemicalOutput(), r.getRightChemicalOutput());
        });
    }

    @Override
    public BasicElectrolysisRecipe onInitialize(@Nullable BasicElectrolysisRecipe recipe) throws UnsupportedRecipeException {
        super.onInitialize(recipe);
        if (recipe == null) {
            return new BasicElectrolysisRecipe(MekanismRecipeUtils.from(Fluids.WATER, 10), 1,
                    new ChemicalStack(MekanismChemicals.OXYGEN, 10), new ChemicalStack(MekanismChemicals.OXYGEN, 10));
        }
        energyMultiplier.set(recipe.getEnergyMultiplier());
        return recipe;
    }

    @Override
    public boolean isValid(BasicElectrolysisRecipe recipe) {
        return true;
    }

    @Override
    public Object getEmiRecipe(BasicElectrolysisRecipe recipe) throws UnsupportedViewerException {
        return new ElectrolysisEmiRecipe((MekanismEmiRecipeCategory) getEmiCategory(CraftTweakerGUI.rl(MekanismAPI.MEKANISM_MODID, "separating")), new RecipeHolder<>(nullRl(), recipe));
    }

    @Override
    public String getCraftTweakerString(BasicElectrolysisRecipe recipe, String id) {
        return "<recipetype:mekanism:separating>.addRecipe(\"" + id + "\", " + NeoLoaderUtils.getCTString(MekanismRecipeUtils.of(recipe.getInput())) + ", " + MekanismRecipeUtils.getCTString(recipe.getLeftChemicalOutput()) + ", " + MekanismRecipeUtils.getCTString(recipe.getRightChemicalOutput()) + ", " + recipe.getEnergyMultiplier() + ");";
    }

    @Override
    public ItemStack getMainOutput(BasicElectrolysisRecipe recipe) {
        return new ItemStack(MekanismBlocks.ELECTROLYTIC_SEPARATOR);
    }
}
