package de.bommels05.ctgui.compat.mekanism;

import com.google.gson.JsonObject;
import de.bommels05.ctgui.api.SupportedRecipeType;
import de.bommels05.ctgui.api.UnsupportedRecipeException;
import de.bommels05.ctgui.api.UnsupportedViewerException;
import mekanism.api.JsonConstants;
import mekanism.api.MekanismAPI;
import mekanism.api.SerializerHelper;
import mekanism.api.chemical.gas.GasStack;
import mekanism.api.recipes.ingredients.creator.IngredientCreatorAccess;
import mekanism.common.recipe.impl.GasConversionIRecipe;
import mekanism.common.registries.MekanismGases;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class GasConvertingRecipeType extends SupportedRecipeType<GasConversionIRecipe> {

    public GasConvertingRecipeType() {
        super(new ResourceLocation(MekanismAPI.MEKANISM_MODID, "gas_conversion"));

        addAreaScrollAmountEmptyRightClick(6, 24, 17, 17, (r, am) -> {
            return new GasConversionIRecipe(r.getId(), MekanismRecipeUtils.of(convertToUnset(am)), r.getOutput(null));
        }, r -> {
            return convertUnset(MekanismRecipeUtils.of(r.getInput()));
        });
        addAreaScrollAmountEmptyRightClick(111, 1, 18, 60, (r, input) -> {
            GasStack stack = input.toStack();
            return new GasConversionIRecipe(r.getId(), r.getInput(), stack.getType() == r.getOutput(null).getType() ? stack : new GasStack(stack, r.getOutput(null).getAmount()));
        }, r -> {
            return new ChemicalAmountedIngredient<>(r.getOutput(null));
        }, () -> new ChemicalAmountedIngredient<>(new GasStack(MekanismGases.OXYGEN.get(), 100)), MekanismRecipeUtils::chemicalAmountSetter);
    }

    @Override
    public GasConversionIRecipe onInitialize(@Nullable GasConversionIRecipe recipe) throws UnsupportedRecipeException {
        super.onInitialize(recipe);
        if (recipe == null) {
            return new GasConversionIRecipe(nullRl(), IngredientCreatorAccess.item().from(UNSET), new GasStack(MekanismGases.OXYGEN.get(), 100));
        }
        return recipe;
    }

    @Override
    public boolean isValid(GasConversionIRecipe recipe) {
        return !recipe.getInput().test(UNSET);
    }

    @Override
    public Object getEmiRecipe(GasConversionIRecipe recipe) throws UnsupportedViewerException {
        throw new UnsupportedViewerException();
    }

    @Override
    public String getCraftTweakerString(GasConversionIRecipe recipe, String id) {
        return "<recipetype:mekanism:gas_conversion>.addRecipe(\"" + id + "\", " + getCTString(MekanismRecipeUtils.of(recipe.getInput())) + ", " + MekanismRecipeUtils.getCTString(recipe.getOutput(null)) + ");";
    }

    @Override
    public JsonObject getRecipeJson(GasConversionIRecipe recipe) {
        JsonObject json = new JsonObject();
        json.add(JsonConstants.INPUT, recipe.getInput().serialize());
        json.add(JsonConstants.OUTPUT, SerializerHelper.serializeGasStack(recipe.getOutput(null)));
        return json;
    }

    @Override
    public GasConversionIRecipe getWithId(GasConversionIRecipe r, ResourceLocation id) {
        return new GasConversionIRecipe(id, r.getInput(), r.getOutput(null));
    }

    @Override
    public ItemStack getMainOutput(GasConversionIRecipe recipe) {
        return convertUnset(MekanismRecipeUtils.of(recipe.getInput()).asStack());
    }
}