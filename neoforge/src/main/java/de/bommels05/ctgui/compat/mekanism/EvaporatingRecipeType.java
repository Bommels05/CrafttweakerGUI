package de.bommels05.ctgui.compat.mekanism;

import de.bommels05.ctgui.NeoLoaderUtils;
import de.bommels05.ctgui.api.FluidAmountedIngredient;
import de.bommels05.ctgui.api.SupportedRecipeType;
import de.bommels05.ctgui.api.UnsupportedRecipeException;
import de.bommels05.ctgui.api.UnsupportedViewerException;
import mekanism.api.MekanismAPI;
import mekanism.api.recipes.basic.BasicFluidToFluidRecipe;
import mekanism.api.recipes.ingredients.creator.IngredientCreatorAccess;
import mekanism.client.recipe_viewer.emi.MekanismEmiRecipeCategory;
import mekanism.client.recipe_viewer.emi.recipe.FluidToFluidEmiRecipe;
import mekanism.common.registries.MekanismBlocks;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;

public class EvaporatingRecipeType extends SupportedRecipeType<BasicFluidToFluidRecipe> {

    public EvaporatingRecipeType() {
        super(new ResourceLocation(MekanismAPI.MEKANISM_MODID, "evaporating"));

        addAreaScrollAmountEmptyRightClick(3, 1, 18, 60, (r, stack) -> {
            return new BasicFluidToFluidRecipe(MekanismRecipeUtils.toIngredientKeepAmount(stack, r.getInput()), r.getOutputRaw());
        }, r -> {
            return MekanismRecipeUtils.of(r.getInput());
        }, () -> new FluidAmountedIngredient(new FluidStack(Fluids.WATER, 1)), NeoLoaderUtils::limitedFluidAmountSetter);
        addAreaScrollAmountEmptyRightClick(149, 1, 18, 60, (r, input) -> {
            FluidStack stack = input.toStack();
            return new BasicFluidToFluidRecipe(r.getInput(), stack.getFluid() == r.getOutputRaw().getFluid() ? stack : new FluidStack(stack, r.getOutputRaw().getAmount()));
        }, r -> {
            return new FluidAmountedIngredient(r.getOutputRaw());
        }, () -> new FluidAmountedIngredient(new FluidStack(Fluids.WATER, 1)), NeoLoaderUtils::limitedFluidAmountSetter);
    }

    @Override
    public BasicFluidToFluidRecipe onInitialize(@Nullable BasicFluidToFluidRecipe recipe) throws UnsupportedRecipeException {
        super.onInitialize(recipe);
        if (recipe == null) {
            return new BasicFluidToFluidRecipe(IngredientCreatorAccess.fluid().from(Fluids.WATER, 1), new FluidStack(Fluids.WATER, 1));
        }
        return recipe;
    }

    @Override
    public boolean isValid(BasicFluidToFluidRecipe recipe) {
        return true;
    }

    @Override
    public Object getEmiRecipe(BasicFluidToFluidRecipe recipe) throws UnsupportedViewerException {
        return new FluidToFluidEmiRecipe((MekanismEmiRecipeCategory) getEmiCategory(new ResourceLocation(MekanismAPI.MEKANISM_MODID, "evaporating")), new RecipeHolder<>(nullRl(), recipe));
    }

    @Override
    public String getCraftTweakerString(BasicFluidToFluidRecipe recipe, String id) {
        return "<recipetype:mekanism:evaporating>.addRecipe(\"" + id + "\", " + NeoLoaderUtils.getCTString(MekanismRecipeUtils.of(recipe.getInput())) + ", " + NeoLoaderUtils.getCTString(recipe.getOutputRaw()) + ");";
    }

    @Override
    public ItemStack getMainOutput(BasicFluidToFluidRecipe recipe) {
        return new ItemStack(MekanismBlocks.THERMAL_EVAPORATION_CONTROLLER);
    }
}
