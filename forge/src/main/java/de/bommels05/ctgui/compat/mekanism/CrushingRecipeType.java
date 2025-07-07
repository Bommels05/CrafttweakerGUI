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
import mekanism.common.recipe.impl.CrushingIRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class CrushingRecipeType extends SupportedRecipeType<CrushingIRecipe> {

    public CrushingRecipeType() {
        super(new ResourceLocation(MekanismAPI.MEKANISM_MODID, "crusher"));

        addAreaScrollAmountEmptyRightClick(35, 0, 17, 17, (r, am) -> {
            return new CrushingIRecipe(r.getId(), MekanismRecipeUtils.of(convertToUnset(am)), r.getResultItem(regAccess()));
        }, r -> {
            return convertUnset(MekanismRecipeUtils.of(r.getInput()));
        });
        addAreaScrollAmountEmptyRightClick(87, 18, 17, 17, (r, am) -> {
            return new CrushingIRecipe(r.getId(), r.getInput(), convertToUnset(am.asStack()));
        }, r -> {
            return AmountedIngredient.of(convertUnset(r.getResultItem(regAccess())));
        });
    }

    @Override
    public CrushingIRecipe onInitialize(@Nullable CrushingIRecipe recipe) throws UnsupportedRecipeException {
        super.onInitialize(recipe);
        if (recipe == null) {
            return new CrushingIRecipe(nullRl(), IngredientCreatorAccess.item().from(UNSET), UNSET);
        }
        return recipe;
    }

    @Override
    public boolean isValid(CrushingIRecipe recipe) {
        return !recipe.getInput().test(UNSET) && !ItemStack.isSameItemSameTags(recipe.getResultItem(regAccess()), UNSET);
    }

    @Override
    public Object getEmiRecipe(CrushingIRecipe recipe) throws UnsupportedViewerException {
        throw new UnsupportedViewerException();
    }

    @Override
    public String getCraftTweakerString(CrushingIRecipe recipe, String id) {
        return "<recipetype:mekanism:crushing>.addRecipe(\"" + id + "\", " + getCTString(MekanismRecipeUtils.of(recipe.getInput())) + ", " + getCTString(recipe.getResultItem(regAccess())) + ");";
    }

    @Override
    public JsonObject getRecipeJson(CrushingIRecipe recipe) {
        JsonObject json = new JsonObject();
        json.add(JsonConstants.INPUT, recipe.getInput().serialize());
        json.add(JsonConstants.OUTPUT, SerializerHelper.serializeItemStack(recipe.getOutput(null)));
        return json;
    }

    @Override
    public CrushingIRecipe getWithId(CrushingIRecipe r, ResourceLocation id) {
        return new CrushingIRecipe(id, r.getInput(), r.getResultItem(regAccess()));
    }
}
