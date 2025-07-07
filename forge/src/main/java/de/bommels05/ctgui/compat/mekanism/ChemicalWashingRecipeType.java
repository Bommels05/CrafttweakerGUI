package de.bommels05.ctgui.compat.mekanism;

import com.google.gson.JsonObject;
import de.bommels05.ctgui.ForgeLoaderUtils;
import de.bommels05.ctgui.api.FluidAmountedIngredient;
import de.bommels05.ctgui.api.SupportedRecipeType;
import de.bommels05.ctgui.api.UnsupportedRecipeException;
import de.bommels05.ctgui.api.UnsupportedViewerException;
import mekanism.api.JsonConstants;
import mekanism.api.MekanismAPI;
import mekanism.api.SerializerHelper;
import mekanism.api.chemical.slurry.SlurryStack;
import mekanism.api.recipes.ingredients.creator.IngredientCreatorAccess;
import mekanism.common.recipe.impl.FluidSlurryToSlurryIRecipe;
import mekanism.common.registries.MekanismBlocks;
import mekanism.common.registries.MekanismSlurries;
import mekanism.common.resource.PrimaryResource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;

public class ChemicalWashingRecipeType extends SupportedRecipeType<FluidSlurryToSlurryIRecipe> {

    public ChemicalWashingRecipeType() {
        super(new ResourceLocation(MekanismAPI.MEKANISM_MODID, "chemical_washer"));

        addAreaScrollAmountEmptyRightClick(0, 0, 18, 60, (r, stack) -> {
            return new FluidSlurryToSlurryIRecipe(r.getId(), MekanismRecipeUtils.toIngredientKeepAmount(stack, r.getFluidInput()), r.getChemicalInput(), r.getOutput(null, null));
        }, r -> {
            return MekanismRecipeUtils.of(r.getFluidInput());
        }, () -> new FluidAmountedIngredient(new FluidStack(Fluids.WATER, 5)), ForgeLoaderUtils::limitedFluidAmountSetter);
        addAreaScrollAmountEmptyRightClick(21, 0, 18, 60, (r, stack) -> {
            return new FluidSlurryToSlurryIRecipe(r.getId(), r.getFluidInput(), MekanismRecipeUtils.toIngredientKeepAmount(stack, r.getChemicalInput()), r.getOutput(null, null));
        }, r -> {
            return MekanismRecipeUtils.of(r.getChemicalInput());
        }, () -> new ChemicalAmountedIngredient<>(new SlurryStack(MekanismSlurries.PROCESSED_RESOURCES.get(PrimaryResource.IRON).getDirtySlurry(), 1)),
                MekanismRecipeUtils::limitedChemicalAmountSetter);
        addAreaScrollAmountEmptyRightClick(124, 0, 18, 60, (r, input) -> {
                    SlurryStack stack = input.toStack();
            return new FluidSlurryToSlurryIRecipe(r.getId(), r.getFluidInput(), r.getChemicalInput(), stack.getType() == r.getOutput(null, null).getType() ? stack : new SlurryStack(stack, r.getOutput(null, null).getAmount()));
        }, r -> {
            return new ChemicalAmountedIngredient<>(r.getOutput(null, null));
        }, () -> new ChemicalAmountedIngredient<>(new SlurryStack(MekanismSlurries.PROCESSED_RESOURCES.get(PrimaryResource.IRON).getCleanSlurry(), 1)),
                MekanismRecipeUtils::limitedChemicalAmountSetter);
    }

    @Override
    public FluidSlurryToSlurryIRecipe onInitialize(@Nullable FluidSlurryToSlurryIRecipe recipe) throws UnsupportedRecipeException {
        super.onInitialize(recipe);
        if (recipe == null) {
            return new FluidSlurryToSlurryIRecipe(nullRl(), IngredientCreatorAccess.fluid().from(Fluids.WATER, 5),
                    IngredientCreatorAccess.slurry().from(MekanismSlurries.PROCESSED_RESOURCES.get(PrimaryResource.IRON).getDirtySlurry(), 1), new SlurryStack(MekanismSlurries.PROCESSED_RESOURCES.get(PrimaryResource.IRON).getCleanSlurry(), 1));
        }
        return recipe;
    }

    @Override
    public boolean isValid(FluidSlurryToSlurryIRecipe recipe) {
        return true;
    }

    @Override
    public Object getEmiRecipe(FluidSlurryToSlurryIRecipe recipe) throws UnsupportedViewerException {
        throw new UnsupportedViewerException();
    }

    @Override
    public String getCraftTweakerString(FluidSlurryToSlurryIRecipe recipe, String id) {
        return "<recipetype:mekanism:washing>.addRecipe(\"" + id + "\", " + ForgeLoaderUtils.getCTString(MekanismRecipeUtils.of(recipe.getFluidInput())) + ", " + MekanismRecipeUtils.getCTString(recipe.getChemicalInput()) + ", " + MekanismRecipeUtils.getCTString(recipe.getOutput(null, null)) + ");";
    }

    @Override
    public JsonObject getRecipeJson(FluidSlurryToSlurryIRecipe recipe) {
        JsonObject json = new JsonObject();
        json.add(JsonConstants.FLUID_INPUT, recipe.getFluidInput().serialize());
        json.add(JsonConstants.SLURRY_INPUT, recipe.getChemicalInput().serialize());
        json.add(JsonConstants.OUTPUT, SerializerHelper.serializeSlurryStack(recipe.getOutput(null, null)));
        return json;
    }

    @Override
    public FluidSlurryToSlurryIRecipe getWithId(FluidSlurryToSlurryIRecipe r, ResourceLocation id) {
        return new FluidSlurryToSlurryIRecipe(id, r.getFluidInput(), r.getChemicalInput(), r.getOutput(null, null));
    }

    @Override
    public ItemStack getMainOutput(FluidSlurryToSlurryIRecipe recipe) {
        return new ItemStack(MekanismBlocks.CHEMICAL_WASHER);
    }
}
