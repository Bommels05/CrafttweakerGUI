package de.bommels05.ctgui.compat.mekanism;

import com.google.gson.JsonObject;
import de.bommels05.ctgui.api.AmountedIngredient;
import de.bommels05.ctgui.api.SupportedRecipeType;
import de.bommels05.ctgui.api.UnsupportedRecipeException;
import de.bommels05.ctgui.api.UnsupportedViewerException;
import mekanism.api.JsonConstants;
import mekanism.api.MekanismAPI;
import mekanism.api.SerializerHelper;
import mekanism.api.chemical.ChemicalType;
import mekanism.api.chemical.gas.Gas;
import mekanism.api.chemical.gas.GasStack;
import mekanism.api.recipes.ingredients.creator.IngredientCreatorAccess;
import mekanism.common.recipe.impl.ChemicalCrystallizerIRecipe;
import mekanism.common.registries.MekanismGases;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class CrystallizingRecipeType extends SupportedRecipeType<ChemicalCrystallizerIRecipe> {

    @SuppressWarnings("unchecked")
    public CrystallizingRecipeType() {
        super(new ResourceLocation(MekanismAPI.MEKANISM_MODID, "chemical_crystallizer"));

        addAreaScrollAmountEmptyRightClick(124, 54, 17, 17, (r, am) -> {
            return new ChemicalCrystallizerIRecipe(r.getId(), r.getInput(), convertToUnset(am.asStack()));
        }, r -> {
            return AmountedIngredient.of(convertUnset(r.getResultItem(regAccess())));
        });
        addAreaScrollAmountEmptyRightClick(2, 1, 18, 60, (r, stack) -> {
            return new ChemicalCrystallizerIRecipe(r.getId(), MekanismRecipeUtils.toIngredientKeepAmount(stack, r.getInput()), r.getResultItem(regAccess()));
        }, r -> {
            return (ChemicalAmountedIngredient<GasStack, Gas>) MekanismRecipeUtils.of(r.getInput());
        }, () -> new ChemicalAmountedIngredient<>(new GasStack(MekanismGases.OXYGEN.get(), 100)), MekanismRecipeUtils::chemicalAmountSetter);
    }

    @Override
    public ChemicalCrystallizerIRecipe onInitialize(@Nullable ChemicalCrystallizerIRecipe recipe) throws UnsupportedRecipeException {
        super.onInitialize(recipe);
        if (recipe == null) {
            return new ChemicalCrystallizerIRecipe(nullRl(), IngredientCreatorAccess.gas().from(MekanismGases.OXYGEN, 100), UNSET);
        }
        return recipe;
    }

    @Override
    public boolean isValid(ChemicalCrystallizerIRecipe recipe) {
        return !ItemStack.isSameItemSameTags(recipe.getResultItem(regAccess()), UNSET);
    }

    @Override
    public Object getEmiRecipe(ChemicalCrystallizerIRecipe recipe) throws UnsupportedViewerException {
        throw new UnsupportedViewerException();
    }

    @Override
    public String getCraftTweakerString(ChemicalCrystallizerIRecipe recipe, String id) {
        return "<recipetype:mekanism:crystallizing>.addRecipe(\"" + id + "\", " + MekanismRecipeUtils.getCTString(recipe.getInput()) + ", " + getCTString(recipe.getResultItem(regAccess())) + ");";
    }

    @Override
    public JsonObject getRecipeJson(ChemicalCrystallizerIRecipe recipe) {
        JsonObject json = new JsonObject();
        json.addProperty(JsonConstants.CHEMICAL_TYPE, ChemicalType.getTypeFor(recipe.getInput()).getSerializedName());
        json.add(JsonConstants.INPUT, recipe.getInput().serialize());
        json.add(JsonConstants.OUTPUT, SerializerHelper.serializeItemStack(recipe.getOutput(null)));
        return json;
    }

    @Override
    public ChemicalCrystallizerIRecipe getWithId(ChemicalCrystallizerIRecipe r, ResourceLocation id) {
        return new ChemicalCrystallizerIRecipe(id, r.getInput(), r.getResultItem(regAccess()));
    }
}
