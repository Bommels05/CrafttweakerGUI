package de.bommels05.ctgui.compat.mekanism;

import de.bommels05.ctgui.api.AmountedIngredient;
import de.bommels05.ctgui.api.SupportedRecipeType;
import de.bommels05.ctgui.api.UnsupportedRecipeException;
import de.bommels05.ctgui.api.UnsupportedViewerException;
import mekanism.api.MekanismAPI;
import mekanism.api.recipes.basic.BasicCombinerRecipe;
import mekanism.api.recipes.ingredients.creator.IngredientCreatorAccess;
import mekanism.client.recipe_viewer.emi.MekanismEmiRecipeCategory;
import mekanism.client.recipe_viewer.emi.recipe.CombinerEmiRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.jetbrains.annotations.Nullable;

public class CombiningRecipeType extends SupportedRecipeType<BasicCombinerRecipe> {

    public CombiningRecipeType() {
        super(new ResourceLocation(MekanismAPI.MEKANISM_MODID, "combining"));

        addAreaScrollAmountEmptyRightClick(35, 0, 17, 17, (r, am) -> {
            return new BasicCombinerRecipe(MekanismRecipeUtils.of(convertToUnset(am)), r.getExtraInput(), r.getOutputRaw());
        }, r -> {
            return convertUnset(MekanismRecipeUtils.of(r.getMainInput()));
        });
        addAreaScrollAmountEmptyRightClick(35, 36, 17, 17, (r, am) -> {
            return new BasicCombinerRecipe(r.getMainInput(), MekanismRecipeUtils.of(convertToUnset(am)), r.getOutputRaw());
        }, r -> {
            return convertUnset(MekanismRecipeUtils.of(r.getExtraInput()));
        });
        addAreaScrollAmountEmptyRightClick(87, 18, 17, 17, (r, am) -> {
            return new BasicCombinerRecipe(r.getMainInput(), r.getExtraInput(), convertToUnset(am.asStack()));
        }, r -> {
            return AmountedIngredient.of(convertUnset(r.getOutputRaw()));
        });
    }

    @Override
    public BasicCombinerRecipe onInitialize(@Nullable BasicCombinerRecipe recipe) throws UnsupportedRecipeException {
        super.onInitialize(recipe);
        if (recipe == null) {
            return new BasicCombinerRecipe(IngredientCreatorAccess.item().from(UNSET), IngredientCreatorAccess.item().from(UNSET), UNSET);
        }
        return recipe;
    }

    @Override
    public boolean isValid(BasicCombinerRecipe recipe) {
        return !recipe.getMainInput().test(UNSET) && !recipe.getExtraInput().test(UNSET) && !ItemStack.isSameItemSameTags(recipe.getOutputRaw(), UNSET);
    }

    @Override
    public Object getEmiRecipe(BasicCombinerRecipe recipe) throws UnsupportedViewerException {
        return new CombinerEmiRecipe((MekanismEmiRecipeCategory) getEmiCategory(new ResourceLocation(MekanismAPI.MEKANISM_MODID, "combining")), new RecipeHolder<>(nullRl(), recipe));
    }

    @Override
    public String getCraftTweakerString(BasicCombinerRecipe recipe, String id) {
        return "<recipetype:mekanism:combining>.addRecipe(\"" + id + "\", " + getCTString(MekanismRecipeUtils.of(recipe.getMainInput())) + ", " + getCTString(MekanismRecipeUtils.of(recipe.getExtraInput())) + ", " + getCTString(recipe.getOutputRaw()) + ");";
    }
}
