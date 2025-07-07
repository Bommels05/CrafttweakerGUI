package de.bommels05.ctgui.compat.mekanism;

import com.google.gson.JsonObject;
import de.bommels05.ctgui.api.SupportedRecipeType;
import de.bommels05.ctgui.api.UnsupportedRecipeException;
import de.bommels05.ctgui.api.UnsupportedViewerException;
import mekanism.api.JsonConstants;
import mekanism.api.MekanismAPI;
import mekanism.api.SerializerHelper;
import mekanism.api.chemical.ChemicalType;
import mekanism.api.math.FloatingLong;
import mekanism.api.recipes.ingredients.creator.IngredientCreatorAccess;
import mekanism.common.recipe.impl.EnergyConversionIRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class EnergyConvertingRecipeType extends SupportedRecipeType<EnergyConversionIRecipe> {

    public EnergyConvertingRecipeType() {
        super(new ResourceLocation(MekanismAPI.MEKANISM_MODID, "energy_conversion"));

        addAreaScrollAmountEmptyRightClick(6, 24, 17, 17, (r, am) -> {
            return new EnergyConversionIRecipe(r.getId(), MekanismRecipeUtils.of(convertToUnset(am)), r.getOutput(ItemStack.EMPTY));
        }, r -> {
            return convertUnset(MekanismRecipeUtils.of(r.getInput()));
        });
        addAreaScrollAmountEmptyRightClick(111, 1, 18, 60, (r, energy) -> {
            return new EnergyConversionIRecipe(r.getId(), r.getInput(), FloatingLong.create(Math.max(1, energy)));
        }, r -> {
            return (int) r.getOutput(null).doubleValue();
        }, () -> 3000, (energy, up) -> {
            return (int) (energy + (getFluidScrollAmount(up) * 2.5));
        });
    }

    @Override
    public EnergyConversionIRecipe onInitialize(@Nullable EnergyConversionIRecipe recipe) throws UnsupportedRecipeException {
        super.onInitialize(recipe);
        if (recipe == null) {
            return new EnergyConversionIRecipe(nullRl(), IngredientCreatorAccess.item().from(UNSET), FloatingLong.create(3000 * 2.5));
        }
        return recipe;
    }

    @Override
    public boolean isValid(EnergyConversionIRecipe recipe) {
        return !recipe.getInput().test(UNSET);
    }

    @Override
    public Object getEmiRecipe(EnergyConversionIRecipe recipe) throws UnsupportedViewerException {
        throw new UnsupportedViewerException();
    }

    @Override
    public String getCraftTweakerString(EnergyConversionIRecipe recipe, String id) {
        return "<recipetype:mekanism:energy_conversion>.addRecipe(\"" + id + "\", " + getCTString(MekanismRecipeUtils.of(recipe.getInput())) + ", " + (int) recipe.getOutput(ItemStack.EMPTY).doubleValue() + ");";
    }

    @Override
    public JsonObject getRecipeJson(EnergyConversionIRecipe recipe) {
        JsonObject json = new JsonObject();
        json.add(JsonConstants.INPUT, recipe.getInput().serialize());
        json.addProperty(JsonConstants.OUTPUT, recipe.getOutput(null));
        return json;
    }

    @Override
    public EnergyConversionIRecipe getWithId(EnergyConversionIRecipe r, ResourceLocation id) {
        return new EnergyConversionIRecipe(id, r.getInput(), r.getOutput(null));
    }

    @Override
    public ItemStack getMainOutput(EnergyConversionIRecipe recipe) {
        return convertUnset(MekanismRecipeUtils.of(recipe.getInput()).asStack());
    }
}
