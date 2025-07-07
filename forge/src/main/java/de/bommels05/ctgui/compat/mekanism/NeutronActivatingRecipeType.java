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
import mekanism.common.recipe.impl.ActivatingIRecipe;
import mekanism.common.registries.MekanismBlocks;
import mekanism.common.registries.MekanismGases;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class NeutronActivatingRecipeType extends SupportedRecipeType<ActivatingIRecipe> {

    public NeutronActivatingRecipeType() {
        super(new ResourceLocation(MekanismAPI.MEKANISM_MODID, "solar_neutron_activator"));

        addAreaScrollAmountEmptyRightClick(21, 0, 18, 60, (r, stack) -> {
            return new ActivatingIRecipe(r.getId(), MekanismRecipeUtils.toIngredientKeepAmount(stack, r.getInput()), r.getOutput(null));
        }, r -> {
            return MekanismRecipeUtils.of(r.getInput());
        }, () -> new ChemicalAmountedIngredient<>(new GasStack(MekanismGases.OXYGEN.get(), 1)), MekanismRecipeUtils::limitedChemicalAmountSetter);
        addAreaScrollAmountEmptyRightClick(129, 0, 18, 60, (r, input) -> {
            GasStack stack = input.toStack();
            return new ActivatingIRecipe(r.getId(), r.getInput(), stack.getType() == r.getOutput(null).getType() ? stack : new GasStack(stack, r.getOutput(null).getAmount()));
        }, r -> {
            return new ChemicalAmountedIngredient<>(r.getOutput(null));
        }, () -> new ChemicalAmountedIngredient<>(new GasStack(MekanismGases.OXYGEN.get(), 1)), MekanismRecipeUtils::limitedChemicalAmountSetter);
    }

    @Override
    public ActivatingIRecipe onInitialize(@Nullable ActivatingIRecipe recipe) throws UnsupportedRecipeException {
        super.onInitialize(recipe);
        if (recipe == null) {
            return new ActivatingIRecipe(nullRl(), IngredientCreatorAccess.gas().from(MekanismGases.OXYGEN, 1), new GasStack(MekanismGases.OXYGEN.get(), 1));
        }
        return recipe;
    }

    @Override
    public boolean isValid(ActivatingIRecipe recipe) {
        return true;
    }

    @Override
    public Object getEmiRecipe(ActivatingIRecipe recipe) throws UnsupportedViewerException {
        throw new UnsupportedViewerException();
    }

    @Override
    public String getCraftTweakerString(ActivatingIRecipe recipe, String id) {
        return "<recipetype:mekanism:activating>.addRecipe(\"" + id + "\", " + MekanismRecipeUtils.getCTString(recipe.getInput()) + ", " + MekanismRecipeUtils.getCTString(recipe.getOutput(null)) + ");";
    }

    @Override
    public JsonObject getRecipeJson(ActivatingIRecipe recipe) {
        JsonObject json = new JsonObject();
        json.add(JsonConstants.INPUT, recipe.getInput().serialize());
        json.add(JsonConstants.OUTPUT, SerializerHelper.serializeGasStack(recipe.getOutput(null)));
        return json;
    }

    @Override
    public ActivatingIRecipe getWithId(ActivatingIRecipe r, ResourceLocation id) {
        return new ActivatingIRecipe(id, r.getInput(), r.getOutput(null));
    }

    @Override
    public ItemStack getMainOutput(ActivatingIRecipe recipe) {
        return new ItemStack(MekanismBlocks.SOLAR_NEUTRON_ACTIVATOR);
    }
}
