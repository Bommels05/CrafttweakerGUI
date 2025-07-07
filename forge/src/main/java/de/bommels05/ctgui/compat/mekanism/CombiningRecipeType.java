package de.bommels05.ctgui.compat.mekanism;

import com.google.gson.JsonObject;
import de.bommels05.ctgui.api.AmountedIngredient;
import de.bommels05.ctgui.api.SupportedRecipeType;
import de.bommels05.ctgui.api.UnsupportedRecipeException;
import de.bommels05.ctgui.api.UnsupportedViewerException;
import mekanism.api.JsonConstants;
import mekanism.api.MekanismAPI;
import mekanism.api.SerializerHelper;
import mekanism.api.recipes.ingredients.creator.IngredientCreatorAccess;
import mekanism.common.recipe.impl.CombinerIRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class CombiningRecipeType extends SupportedRecipeType<CombinerIRecipe> {

    public CombiningRecipeType() {
        super(new ResourceLocation(MekanismAPI.MEKANISM_MODID, "combiner"));

        addAreaScrollAmountEmptyRightClick(35, 0, 17, 17, (r, am) -> {
            return new CombinerIRecipe(r.getId(), MekanismRecipeUtils.of(convertToUnset(am)), r.getExtraInput(), r.getResultItem(regAccess()));
        }, r -> {
            return convertUnset(MekanismRecipeUtils.of(r.getMainInput()));
        });
        addAreaScrollAmountEmptyRightClick(35, 36, 17, 17, (r, am) -> {
            return new CombinerIRecipe(r.getId(), r.getMainInput(), MekanismRecipeUtils.of(convertToUnset(am)), r.getResultItem(regAccess()));
        }, r -> {
            return convertUnset(MekanismRecipeUtils.of(r.getExtraInput()));
        });
        addAreaScrollAmountEmptyRightClick(87, 18, 17, 17, (r, am) -> {
            return new CombinerIRecipe(r.getId(), r.getMainInput(), r.getExtraInput(), convertToUnset(am.asStack()));
        }, r -> {
            return AmountedIngredient.of(convertUnset(r.getResultItem(regAccess())));
        });
    }

    @Override
    public CombinerIRecipe onInitialize(@Nullable CombinerIRecipe recipe) throws UnsupportedRecipeException {
        super.onInitialize(recipe);
        if (recipe == null) {
            return new CombinerIRecipe(nullRl(), IngredientCreatorAccess.item().from(UNSET), IngredientCreatorAccess.item().from(UNSET), UNSET);
        }
        return recipe;
    }

    @Override
    public boolean isValid(CombinerIRecipe recipe) {
        return !recipe.getMainInput().test(UNSET) && !recipe.getExtraInput().test(UNSET) && !ItemStack.isSameItemSameTags(recipe.getResultItem(regAccess()), UNSET);
    }

    @Override
    public Object getEmiRecipe(CombinerIRecipe recipe) throws UnsupportedViewerException {
       throw new UnsupportedViewerException();
    }

    @Override
    public String getCraftTweakerString(CombinerIRecipe recipe, String id) {
        return "<recipetype:mekanism:combining>.addRecipe(\"" + id + "\", " + getCTString(MekanismRecipeUtils.of(recipe.getMainInput())) + ", " + getCTString(MekanismRecipeUtils.of(recipe.getExtraInput())) + ", " + getCTString(recipe.getResultItem(regAccess())) + ");";
    }

    @Override
    public JsonObject getRecipeJson(CombinerIRecipe recipe) {
        JsonObject json = new JsonObject();
        json.add(JsonConstants.MAIN_INPUT, recipe.getMainInput().serialize());
        json.add(JsonConstants.EXTRA_INPUT, recipe.getExtraInput().serialize());
        json.add(JsonConstants.OUTPUT, SerializerHelper.serializeItemStack(recipe.getOutput(null, null)));
        return json;
    }

    @Override
    public CombinerIRecipe getWithId(CombinerIRecipe r, ResourceLocation id) {
        return new CombinerIRecipe(id, r.getMainInput(), r.getExtraInput(), r.getResultItem(regAccess()));
    }
}
