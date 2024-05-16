package de.bommels05.ctgui.compat.mekanism;

import de.bommels05.ctgui.api.SupportedRecipeType;
import de.bommels05.ctgui.api.UnsupportedRecipeException;
import de.bommels05.ctgui.api.UnsupportedViewerException;
import mekanism.api.MekanismAPI;
import mekanism.api.chemical.pigment.PigmentStack;
import mekanism.api.recipes.basic.BasicPigmentMixingRecipe;
import mekanism.api.recipes.ingredients.creator.IngredientCreatorAccess;
import mekanism.api.text.EnumColor;
import mekanism.client.recipe_viewer.emi.MekanismEmiRecipeCategory;
import mekanism.client.recipe_viewer.emi.recipe.PigmentMixerEmiRecipe;
import mekanism.common.registries.MekanismBlocks;
import mekanism.common.registries.MekanismPigments;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.jetbrains.annotations.Nullable;

public class PigmentMixingRecipeType extends SupportedRecipeType<BasicPigmentMixingRecipe> {

    public PigmentMixingRecipeType() {
        super(new ResourceLocation(MekanismAPI.MEKANISM_MODID, "pigment_mixing"));

        addAreaScrollAmountEmptyRightClick(22, 10, 18, 60, (r, stack) -> {
            return new BasicPigmentMixingRecipe(MekanismRecipeUtils.toIngredientKeepAmount(stack, r.getLeftInput()), r.getRightInput(), r.getOutputRaw());
        }, r -> {
            return MekanismRecipeUtils.of(r.getLeftInput());
        }, () -> new ChemicalAmountedIngredient<>(new PigmentStack(MekanismPigments.PIGMENT_COLOR_LOOKUP.get(EnumColor.RED), 1)), MekanismRecipeUtils::limitedChemicalAmountSetter);
        addAreaScrollAmountEmptyRightClick(130, 10, 18, 60, (r, stack) -> {
            return new BasicPigmentMixingRecipe(r.getLeftInput(), MekanismRecipeUtils.toIngredientKeepAmount(stack, r.getRightInput()), r.getOutputRaw());
        }, r -> {
            return MekanismRecipeUtils.of(r.getRightInput());
        }, () -> new ChemicalAmountedIngredient<>(new PigmentStack(MekanismPigments.PIGMENT_COLOR_LOOKUP.get(EnumColor.RED), 1)), MekanismRecipeUtils::limitedChemicalAmountSetter);
        addAreaScrollAmountEmptyRightClick(76, 1, 18, 60, (r, input) -> {
            PigmentStack stack = input.toStack();
            return new BasicPigmentMixingRecipe(r.getLeftInput(), r.getRightInput(), stack.getType() == r.getOutputRaw().getType() ? stack : new PigmentStack(stack, r.getOutputRaw().getAmount()));
        }, r -> {
            return new ChemicalAmountedIngredient<>(r.getOutputRaw());
        }, () -> new ChemicalAmountedIngredient<>(new PigmentStack(MekanismPigments.PIGMENT_COLOR_LOOKUP.get(EnumColor.RED), 2)), MekanismRecipeUtils::limitedChemicalAmountSetter);
    }

    @Override
    public BasicPigmentMixingRecipe onInitialize(@Nullable BasicPigmentMixingRecipe recipe) throws UnsupportedRecipeException {
        super.onInitialize(recipe);
        if (recipe == null) {
            return new BasicPigmentMixingRecipe(IngredientCreatorAccess.pigment().from(MekanismPigments.PIGMENT_COLOR_LOOKUP.get(EnumColor.RED), 1),
                    IngredientCreatorAccess.pigment().from(MekanismPigments.PIGMENT_COLOR_LOOKUP.get(EnumColor.RED), 1),
                    new PigmentStack(MekanismPigments.PIGMENT_COLOR_LOOKUP.get(EnumColor.RED), 2));
        }
        return recipe;
    }

    @Override
    public boolean isValid(BasicPigmentMixingRecipe recipe) {
        return true;
    }

    @Override
    public Object getEmiRecipe(BasicPigmentMixingRecipe recipe) throws UnsupportedViewerException {
        return new PigmentMixerEmiRecipe((MekanismEmiRecipeCategory) getEmiCategory(new ResourceLocation(MekanismAPI.MEKANISM_MODID, "pigment_mixing")), new RecipeHolder<>(nullRl(), recipe));
    }

    @Override
    public String getCraftTweakerString(BasicPigmentMixingRecipe recipe, String id) {
        return "<recipetype:mekanism:pigment_mixing>.addRecipe(\"" + id + "\", " + MekanismRecipeUtils.getCTString(recipe.getLeftInput()) + ", " + MekanismRecipeUtils.getCTString(recipe.getRightInput()) + ", " + MekanismRecipeUtils.getCTString(recipe.getOutputRaw()) + ");";
    }

    @Override
    public ItemStack getMainOutput(BasicPigmentMixingRecipe recipe) {
        return new ItemStack(MekanismBlocks.PIGMENT_MIXER);
    }
}
