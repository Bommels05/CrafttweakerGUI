package de.bommels05.ctgui.compat.mekanism;

import com.google.gson.JsonObject;
import de.bommels05.ctgui.api.AmountedIngredient;
import de.bommels05.ctgui.api.SupportedRecipeType;
import de.bommels05.ctgui.api.UnsupportedRecipeException;
import de.bommels05.ctgui.api.UnsupportedViewerException;
import mekanism.api.JsonConstants;
import mekanism.api.MekanismAPI;
import mekanism.api.SerializerHelper;
import mekanism.api.chemical.gas.GasStack;
import mekanism.api.recipes.ingredients.creator.IngredientCreatorAccess;
import mekanism.common.recipe.impl.InjectingIRecipe;
import mekanism.common.registries.MekanismGases;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class InjectingRecipeType extends SupportedRecipeType<InjectingIRecipe> {
    public InjectingRecipeType() {
        super(new ResourceLocation(MekanismAPI.MEKANISM_MODID, "chemical_injection_chamber"));

        addAreaScrollAmountEmptyRightClick(36, 1, 17, 17, (r, am) -> {
            return new InjectingIRecipe(r.getId(), MekanismRecipeUtils.of(convertToUnset(am)), r.getChemicalInput(), r.getResultItem(regAccess()));
        }, r -> {
            return convertUnset(MekanismRecipeUtils.of(r.getItemInput()));
        });
        addAreaScrollAmountEmptyRightClick(88, 19, 17, 17, (r, am) -> {
            return new InjectingIRecipe(r.getId(), r.getItemInput(), r.getChemicalInput(), convertToUnset(am.asStack()));
        }, r -> {
            return AmountedIngredient.of(convertUnset(r.getResultItem(regAccess())));
        });
        addAreaScrollAmountEmptyRightClick(40, 20, 6, 12, (r, stack) -> {
            return new InjectingIRecipe(r.getId(), r.getItemInput(), MekanismRecipeUtils.toIngredientKeepAmount(stack, r.getChemicalInput()), r.getResultItem(regAccess()));
        }, r -> {
            return MekanismRecipeUtils.of(r.getChemicalInput());
        }, () -> new ChemicalAmountedIngredient<>(new GasStack(MekanismGases.OXYGEN.get(), 1)), MekanismRecipeUtils::limitedChemicalAmountSetter);
    }

    @Override
    public InjectingIRecipe onInitialize(@Nullable InjectingIRecipe recipe) throws UnsupportedRecipeException {
        super.onInitialize(recipe);
        if (recipe == null) {
            return new InjectingIRecipe(nullRl(), IngredientCreatorAccess.item().from(UNSET), IngredientCreatorAccess.gas().from(MekanismGases.OXYGEN, 1), UNSET);
        }
        return recipe;
    }

    @Override
    public boolean isValid(InjectingIRecipe recipe) {
        return !recipe.getItemInput().test(UNSET) && !ItemStack.isSameItemSameTags(recipe.getResultItem(regAccess()), UNSET);
    }

    @Override
    public Object getEmiRecipe(InjectingIRecipe recipe) throws UnsupportedViewerException {
        throw new UnsupportedViewerException();
    }

    @Override
    public String getCraftTweakerString(InjectingIRecipe recipe, String id) {
        return "<recipetype:mekanism:injecting>.addRecipe(\"" + id + "\", " + getCTString(MekanismRecipeUtils.of(recipe.getItemInput())) + ", " + MekanismRecipeUtils.getCTString(recipe.getChemicalInput()) + ", " + getCTString(recipe.getResultItem(regAccess())) + ");";
    }

    @Override
    public JsonObject getRecipeJson(InjectingIRecipe recipe) {
        JsonObject json = new JsonObject();
        json.add(JsonConstants.ITEM_INPUT, recipe.getItemInput().serialize());
        json.add(JsonConstants.CHEMICAL_INPUT, recipe.getChemicalInput().serialize());
        json.add(JsonConstants.OUTPUT, SerializerHelper.serializeItemStack(recipe.getResultItem(regAccess())));
        return json;
    }

    @Override
    public InjectingIRecipe getWithId(InjectingIRecipe r, ResourceLocation id) {
        return new InjectingIRecipe(id, r.getItemInput(), r.getChemicalInput(), r.getResultItem(regAccess()));
    }
}
