package de.bommels05.ctgui.compat.mekanism;

import com.google.gson.JsonObject;
import de.bommels05.ctgui.api.AmountedIngredient;
import de.bommels05.ctgui.api.SupportedRecipeType;
import de.bommels05.ctgui.api.UnsupportedRecipeException;
import de.bommels05.ctgui.api.UnsupportedViewerException;
import de.bommels05.ctgui.screen.RecipeEditScreen;
import mekanism.api.JsonConstants;
import mekanism.api.MekanismAPI;
import mekanism.api.SerializerHelper;
import mekanism.api.recipes.ingredients.creator.IngredientCreatorAccess;
import mekanism.common.recipe.impl.SmeltingIRecipe;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class EnergizedSmeltingRecipeType extends SupportedRecipeType<SmeltingIRecipe> {

    public EnergizedSmeltingRecipeType() {
        super(new ResourceLocation(MekanismAPI.MEKANISM_MODID, "energized_smelter"));

        addAreaScrollAmountEmptyRightClick(35, 0, 17, 17, (r, am) -> {
            return new SmeltingIRecipe(r.getId(), MekanismRecipeUtils.of(convertToUnset(am)), r.getResultItem(regAccess()));
        }, r -> {
            return convertUnset(MekanismRecipeUtils.of(r.getInput()));
        });
        addAreaScrollAmountEmptyRightClick(87, 18, 17, 17, (r, am) -> {
            return new SmeltingIRecipe(r.getId(), r.getInput(), convertToUnset(am.asStack()));
        }, r -> {
            return AmountedIngredient.of(convertUnset(r.getResultItem(regAccess())));
        });
    }

    @Override
    public SmeltingIRecipe onInitialize(@Nullable SmeltingIRecipe recipe) throws UnsupportedRecipeException {
        super.onInitialize(recipe);
        if (recipe == null) {
            return new SmeltingIRecipe(nullRl(), IngredientCreatorAccess.item().from(UNSET), UNSET);
        } else if (Minecraft.getInstance().screen instanceof RecipeEditScreen<?> screen && screen.getOriginalRecipeId().toString().startsWith("minecraft:/mekanism_generated/")) {
            throw new UnsupportedRecipeException(Component.translatable("ctgui.editing.vanilla_smelting_recipe"));
        }
        return recipe;
    }

    @Override
    public boolean isValid(SmeltingIRecipe recipe) {
        return !recipe.getInput().test(UNSET) && !ItemStack.isSameItemSameTags(recipe.getResultItem(regAccess()), UNSET);
    }

    @Override
    public Object getEmiRecipe(SmeltingIRecipe recipe) throws UnsupportedViewerException {
        throw new UnsupportedViewerException();
    }

    @Override
    public String getCraftTweakerString(SmeltingIRecipe recipe, String id) {
        return "<recipetype:mekanism:smelting>.addRecipe(\"" + id + "\", " + getCTString(MekanismRecipeUtils.of(recipe.getInput())) + ", " + getCTString(recipe.getResultItem(regAccess())) + ");";
    }

    @Override
    public JsonObject getRecipeJson(SmeltingIRecipe recipe) {
        JsonObject json = new JsonObject();
        json.add(JsonConstants.INPUT, recipe.getInput().serialize());
        json.add(JsonConstants.OUTPUT, SerializerHelper.serializeItemStack(recipe.getOutput(null)));
        return json;
    }

    @Override
    public SmeltingIRecipe getWithId(SmeltingIRecipe r, ResourceLocation id) {
        return new SmeltingIRecipe(id, r.getInput(), r.getResultItem(regAccess()));
    }
}
