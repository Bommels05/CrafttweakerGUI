package de.bommels05.ctgui.compat.mekanism;

import de.bommels05.ctgui.CraftTweakerGUI;
import de.bommels05.ctgui.api.SupportedRecipeType;
import de.bommels05.ctgui.api.UnsupportedRecipeException;
import de.bommels05.ctgui.api.UnsupportedViewerException;
import mekanism.api.MekanismAPI;
import mekanism.api.chemical.ChemicalStack;
import mekanism.api.recipes.basic.BasicPigmentExtractingRecipe;
import mekanism.api.recipes.ingredients.creator.IngredientCreatorAccess;
import mekanism.api.text.EnumColor;
import mekanism.client.recipe_viewer.emi.MekanismEmiRecipeCategory;
import mekanism.client.recipe_viewer.emi.recipe.PigmentExtractingEmiRecipe;
import mekanism.common.registries.MekanismChemicals;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.jetbrains.annotations.Nullable;

public class PigmentExtractingRecipeType extends SupportedRecipeType<BasicPigmentExtractingRecipe> {

    public PigmentExtractingRecipeType() {
        super(CraftTweakerGUI.rl(MekanismAPI.MEKANISM_MODID, "pigment_extracting"));

        addAreaScrollAmountEmptyRightClick(6, 24, 17, 17, (r, am) -> {
            return new BasicPigmentExtractingRecipe(MekanismRecipeUtils.of(convertToUnset(am)), r.getOutputRaw());
        }, r -> {
            return convertUnset(MekanismRecipeUtils.of(r.getInput()));
        });
        addAreaScrollAmountEmptyRightClick(113, 1, 18, 60, (r, input) -> {
            ChemicalStack stack = input.toStack();
            return new BasicPigmentExtractingRecipe(r.getInput(), stack.getChemical() == r.getOutputRaw().getChemical() ? stack : stack.copyWithAmount(r.getOutputRaw().getAmount()));
        }, r -> {
            return new ChemicalAmountedIngredient(r.getOutputRaw());
        }, () -> new ChemicalAmountedIngredient(new ChemicalStack(MekanismChemicals.PIGMENT_COLOR_LOOKUP.get(EnumColor.RED), 100)), MekanismRecipeUtils::chemicalAmountSetter);
    }

    @Override
    public BasicPigmentExtractingRecipe onInitialize(@Nullable BasicPigmentExtractingRecipe recipe) throws UnsupportedRecipeException {
        super.onInitialize(recipe);
        if (recipe == null) {
            return new BasicPigmentExtractingRecipe(IngredientCreatorAccess.item().from(UNSET), new ChemicalStack(MekanismChemicals.PIGMENT_COLOR_LOOKUP.get(EnumColor.RED), 100));
        }
        return recipe;
    }

    @Override
    public boolean isValid(BasicPigmentExtractingRecipe recipe) {
        return !recipe.getInput().test(UNSET);
    }

    @Override
    public Object getEmiRecipe(BasicPigmentExtractingRecipe recipe) throws UnsupportedViewerException {
        return new PigmentExtractingEmiRecipe((MekanismEmiRecipeCategory) getEmiCategory(CraftTweakerGUI.rl(MekanismAPI.MEKANISM_MODID, "pigment_extracting")), new RecipeHolder<>(nullRl(), recipe));
    }

    @Override
    public String getCraftTweakerString(BasicPigmentExtractingRecipe recipe, String id) {
        return "<recipetype:mekanism:pigment_extracting>.addRecipe(\"" + id + "\", " + getCTString(MekanismRecipeUtils.of(recipe.getInput())) + ", " + MekanismRecipeUtils.getCTString(recipe.getOutputRaw()) + ");";
    }

    @Override
    public ItemStack getMainOutput(BasicPigmentExtractingRecipe recipe) {
        return convertUnset(MekanismRecipeUtils.of(recipe.getInput()).asStack());
    }
}
