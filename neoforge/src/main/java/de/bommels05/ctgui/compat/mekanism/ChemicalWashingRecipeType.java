package de.bommels05.ctgui.compat.mekanism;

import de.bommels05.ctgui.NeoLoaderUtils;
import de.bommels05.ctgui.api.FluidAmountedIngredient;
import de.bommels05.ctgui.api.SupportedRecipeType;
import de.bommels05.ctgui.api.UnsupportedRecipeException;
import de.bommels05.ctgui.api.UnsupportedViewerException;
import mekanism.api.MekanismAPI;
import mekanism.api.chemical.slurry.SlurryStack;
import mekanism.api.recipes.basic.BasicFluidSlurryToSlurryRecipe;
import mekanism.api.recipes.ingredients.creator.IngredientCreatorAccess;
import mekanism.client.recipe_viewer.emi.MekanismEmiRecipeCategory;
import mekanism.client.recipe_viewer.emi.recipe.FluidSlurryToSlurryEmiRecipe;
import mekanism.common.registries.MekanismBlocks;
import mekanism.common.registries.MekanismSlurries;
import mekanism.common.resource.PrimaryResource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;

public class ChemicalWashingRecipeType extends SupportedRecipeType<BasicFluidSlurryToSlurryRecipe> {

    public ChemicalWashingRecipeType() {
        super(new ResourceLocation(MekanismAPI.MEKANISM_MODID, "washing"));

        addAreaScrollAmountEmptyRightClick(0, 0, 18, 60, (r, stack) -> {
            return new BasicFluidSlurryToSlurryRecipe(MekanismRecipeUtils.toIngredientKeepAmount(stack, r.getFluidInput()), r.getChemicalInput(), r.getOutputRaw());
        }, r -> {
            return MekanismRecipeUtils.of(r.getFluidInput());
        }, () -> new FluidAmountedIngredient(new FluidStack(Fluids.WATER, 5)), NeoLoaderUtils::limitedFluidAmountSetter);
        addAreaScrollAmountEmptyRightClick(21, 0, 18, 60, (r, stack) -> {
            return new BasicFluidSlurryToSlurryRecipe(r.getFluidInput(), MekanismRecipeUtils.toIngredientKeepAmount(stack, r.getChemicalInput()), r.getOutputRaw());
        }, r -> {
            return MekanismRecipeUtils.of(r.getChemicalInput());
        }, () -> new ChemicalAmountedIngredient<>(new SlurryStack(MekanismSlurries.PROCESSED_RESOURCES.get(PrimaryResource.IRON).getDirtySlurry(), 1)),
                MekanismRecipeUtils::limitedChemicalAmountSetter);
        addAreaScrollAmountEmptyRightClick(124, 0, 18, 60, (r, input) -> {
            SlurryStack stack = input.toStack();
            return new BasicFluidSlurryToSlurryRecipe(r.getFluidInput(), r.getChemicalInput(), stack.getType() == r.getOutputRaw().getType() ? stack : new SlurryStack(stack, r.getOutputRaw().getAmount()));
        }, r -> {
            return new ChemicalAmountedIngredient<>(r.getOutputRaw());
        }, () -> new ChemicalAmountedIngredient<>(new SlurryStack(MekanismSlurries.PROCESSED_RESOURCES.get(PrimaryResource.IRON).getCleanSlurry(), 1)),
                MekanismRecipeUtils::limitedChemicalAmountSetter);
    }

    @Override
    public BasicFluidSlurryToSlurryRecipe onInitialize(@Nullable BasicFluidSlurryToSlurryRecipe recipe) throws UnsupportedRecipeException {
        super.onInitialize(recipe);
        if (recipe == null) {
            return new BasicFluidSlurryToSlurryRecipe(IngredientCreatorAccess.fluid().from(Fluids.WATER, 5),
                    IngredientCreatorAccess.slurry().from(MekanismSlurries.PROCESSED_RESOURCES.get(PrimaryResource.IRON).getDirtySlurry(), 1), new SlurryStack(MekanismSlurries.PROCESSED_RESOURCES.get(PrimaryResource.IRON).getCleanSlurry(), 1));
        }
        return recipe;
    }

    @Override
    public boolean isValid(BasicFluidSlurryToSlurryRecipe recipe) {
        return true;
    }

    @Override
    public Object getEmiRecipe(BasicFluidSlurryToSlurryRecipe recipe) throws UnsupportedViewerException {
        return new FluidSlurryToSlurryEmiRecipe((MekanismEmiRecipeCategory) getEmiCategory(new ResourceLocation(MekanismAPI.MEKANISM_MODID, "washing")), new RecipeHolder<>(nullRl(), recipe));
    }

    @Override
    public String getCraftTweakerString(BasicFluidSlurryToSlurryRecipe recipe, String id) {
        return "<recipetype:mekanism:washing>.addRecipe(\"" + id + "\", " + NeoLoaderUtils.getCTString(MekanismRecipeUtils.of(recipe.getFluidInput())) + ", " + MekanismRecipeUtils.getCTString(recipe.getChemicalInput()) + ", " + MekanismRecipeUtils.getCTString(recipe.getOutputRaw()) + ");";
    }

    @Override
    public ItemStack getMainOutput(BasicFluidSlurryToSlurryRecipe recipe) {
        return new ItemStack(MekanismBlocks.CHEMICAL_WASHER);
    }
}
