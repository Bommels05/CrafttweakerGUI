package de.bommels05.ctgui.compat.mekanism;

import de.bommels05.ctgui.CraftTweakerGUI;
import de.bommels05.ctgui.NeoLoaderUtils;
import de.bommels05.ctgui.api.FluidAmountedIngredient;
import de.bommels05.ctgui.api.SupportedRecipeType;
import de.bommels05.ctgui.api.UnsupportedRecipeException;
import de.bommels05.ctgui.api.UnsupportedViewerException;
import mekanism.api.MekanismAPI;
import mekanism.api.chemical.ChemicalStack;
import mekanism.api.recipes.basic.BasicWashingRecipe;
import mekanism.api.recipes.ingredients.creator.IngredientCreatorAccess;
import mekanism.client.recipe_viewer.emi.MekanismEmiRecipeCategory;
import mekanism.client.recipe_viewer.emi.recipe.FluidChemicalToChemicalEmiRecipe;
import mekanism.common.registries.MekanismBlocks;
import mekanism.common.registries.MekanismChemicals;
import mekanism.common.resource.PrimaryResource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;
import org.jetbrains.annotations.Nullable;

public class ChemicalWashingRecipeType extends SupportedRecipeType<BasicWashingRecipe> {

    public ChemicalWashingRecipeType() {
        super(CraftTweakerGUI.rl(MekanismAPI.MEKANISM_MODID, "washing"));

        addAreaScrollAmountEmptyRightClick(0, 0, 18, 60, (r, stack) -> {
            return new BasicWashingRecipe(MekanismRecipeUtils.toIngredientKeepAmount(stack, r.getFluidInput()), r.getChemicalInput(), r.getOutputRaw());
        }, r -> {
            return MekanismRecipeUtils.of(r.getFluidInput());
        }, () -> new FluidAmountedIngredient(new FluidStack(Fluids.WATER, 5)), NeoLoaderUtils::limitedFluidAmountSetter);
        addAreaScrollAmountEmptyRightClick(21, 0, 18, 60, (r, stack) -> {
            return new BasicWashingRecipe(r.getFluidInput(), MekanismRecipeUtils.toIngredientKeepAmount(stack, r.getChemicalInput()), r.getOutputRaw());
        }, r -> {
            return MekanismRecipeUtils.of(r.getChemicalInput());
        }, () -> new ChemicalAmountedIngredient(new ChemicalStack(MekanismChemicals.PROCESSED_RESOURCES.get(PrimaryResource.IRON), 1)),
                MekanismRecipeUtils::limitedChemicalAmountSetter);
        addAreaScrollAmountEmptyRightClick(124, 0, 18, 60, (r, input) -> {
                    ChemicalStack stack = input.toStack();
            return new BasicWashingRecipe(r.getFluidInput(), r.getChemicalInput(), stack.getChemical() == r.getOutputRaw().getChemical() ? stack : stack.copyWithAmount(r.getOutputRaw().getAmount()));
        }, r -> {
            return new ChemicalAmountedIngredient(r.getOutputRaw());
        }, () -> new ChemicalAmountedIngredient(new ChemicalStack(MekanismChemicals.PROCESSED_RESOURCES.get(PrimaryResource.IRON).getCleanSlurry(), 1)),
                MekanismRecipeUtils::limitedChemicalAmountSetter);
    }

    @Override
    public BasicWashingRecipe onInitialize(@Nullable BasicWashingRecipe recipe) throws UnsupportedRecipeException {
        super.onInitialize(recipe);
        if (recipe == null) {
            return new BasicWashingRecipe(MekanismRecipeUtils.from(Fluids.WATER, 5),
                    IngredientCreatorAccess.chemicalStack().fromHolder(MekanismChemicals.PROCESSED_RESOURCES.get(PrimaryResource.IRON), 1),
                    new ChemicalStack(MekanismChemicals.PROCESSED_RESOURCES.get(PrimaryResource.IRON).getCleanSlurry(), 1));
        }
        return recipe;
    }

    @Override
    public boolean isValid(BasicWashingRecipe recipe) {
        return true;
    }

    @Override
    public Object getEmiRecipe(BasicWashingRecipe recipe) throws UnsupportedViewerException {
        return new FluidChemicalToChemicalEmiRecipe((MekanismEmiRecipeCategory) getEmiCategory(CraftTweakerGUI.rl(MekanismAPI.MEKANISM_MODID, "washing")), new RecipeHolder<>(nullRl(), recipe));
    }

    @Override
    public String getCraftTweakerString(BasicWashingRecipe recipe, String id) {
        return "<recipetype:mekanism:washing>.addRecipe(\"" + id + "\", " + NeoLoaderUtils.getCTString(MekanismRecipeUtils.of(recipe.getFluidInput())) + ", " + MekanismRecipeUtils.getCTString(recipe.getChemicalInput()) + ", " + MekanismRecipeUtils.getCTString(recipe.getOutputRaw()) + ");";
    }

    @Override
    public ItemStack getMainOutput(BasicWashingRecipe recipe) {
        return new ItemStack(MekanismBlocks.CHEMICAL_WASHER);
    }
}
