package de.bommels05.ctgui.compat.mekanism;

import de.bommels05.ctgui.CraftTweakerGUI;
import de.bommels05.ctgui.api.AmountedIngredient;
import de.bommels05.ctgui.api.SupportedRecipeType;
import de.bommels05.ctgui.api.UnsupportedRecipeException;
import de.bommels05.ctgui.api.UnsupportedViewerException;
import de.bommels05.ctgui.api.option.BooleanRecipeOption;
import mekanism.api.MekanismAPI;
import mekanism.api.chemical.ChemicalStack;
import mekanism.api.recipes.basic.BasicPaintingRecipe;
import mekanism.api.recipes.ingredients.creator.IngredientCreatorAccess;
import mekanism.api.text.EnumColor;
import mekanism.client.recipe_viewer.emi.MekanismEmiRecipeCategory;
import mekanism.client.recipe_viewer.emi.recipe.PaintingEmiRecipe;
import mekanism.common.registries.MekanismChemicals;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.jetbrains.annotations.Nullable;

public class PaintingRecipeType extends SupportedRecipeType<BasicPaintingRecipe> {

    private final BooleanRecipeOption<BasicPaintingRecipe> perTickUsage = new BooleanRecipeOption<>(Component.translatable("ctgui.editing.options.per_tick_usage"), Component.translatable("ctgui.editing.options.per_tick_usage_chemical"));

    public PaintingRecipeType() {
        super(CraftTweakerGUI.rl(MekanismAPI.MEKANISM_MODID, "painting"));

        addAreaScrollAmountEmptyRightClick(20, 22, 17, 17, (r, am) -> {
            return new BasicPaintingRecipe(MekanismRecipeUtils.of(convertToUnset(am)), r.getChemicalInput(), r.getOutputRaw(), r.perTickUsage());
        }, r -> {
            return convertUnset(MekanismRecipeUtils.of(r.getItemInput()));
        });
        addAreaScrollAmountEmptyRightClick(91, 22, 17, 17, (r, am) -> {
            return new BasicPaintingRecipe(r.getItemInput(), r.getChemicalInput(), convertToUnset(am.asStack()), r.perTickUsage());
        }, r -> {
            return AmountedIngredient.of(convertUnset(r.getOutputRaw()));
        });
        addAreaScrollAmountEmptyRightClick(0, 0, 18, 60, (r, stack) -> {
            return new BasicPaintingRecipe(r.getItemInput(), MekanismRecipeUtils.toIngredientKeepAmount(stack, r.getChemicalInput()), r.getOutputRaw(), r.perTickUsage());
        }, r -> {
            return MekanismRecipeUtils.of(r.getChemicalInput());
        }, () -> new ChemicalAmountedIngredient(new ChemicalStack(MekanismChemicals.PIGMENT_COLOR_LOOKUP.get(EnumColor.RED), 50)), (stack, up) ->
                perTickUsage.get() ? MekanismRecipeUtils.limitedChemicalAmountSetter(stack, up) : MekanismRecipeUtils.chemicalAmountSetter(stack, up));
        addOption(perTickUsage, (r, value) -> {
            return new BasicPaintingRecipe(r.getItemInput(), r.getChemicalInput(), r.getOutputRaw(), value);
        });
    }

    @Override
    public BasicPaintingRecipe onInitialize(@Nullable BasicPaintingRecipe recipe) throws UnsupportedRecipeException {
        super.onInitialize(recipe);
        if (recipe == null) {
            return new BasicPaintingRecipe(IngredientCreatorAccess.item().from(UNSET),
                    IngredientCreatorAccess.chemicalStack().fromHolder(MekanismChemicals.PIGMENT_COLOR_LOOKUP.get(EnumColor.RED), 50), UNSET, false);
        }
        perTickUsage.set(recipe.perTickUsage());
        return recipe;
    }

    @Override
    public boolean isValid(BasicPaintingRecipe recipe) {
        return !recipe.getItemInput().test(UNSET) && !ItemStack.isSameItemSameComponents(recipe.getOutputRaw(), UNSET);
    }

    @Override
    public Object getEmiRecipe(BasicPaintingRecipe recipe) throws UnsupportedViewerException {
        return new PaintingEmiRecipe((MekanismEmiRecipeCategory) getEmiCategory(CraftTweakerGUI.rl(MekanismAPI.MEKANISM_MODID, "painting")), new RecipeHolder<>(nullRl(), recipe));
    }

    @Override
    public String getCraftTweakerString(BasicPaintingRecipe recipe, String id) {
        return "<recipetype:mekanism:painting>.addRecipe(\"" + id + "\", " + getCTString(MekanismRecipeUtils.of(recipe.getItemInput())) + ", " + MekanismRecipeUtils.getCTString(recipe.getChemicalInput()) + ", " + getCTString(recipe.getOutputRaw()) + ", " + recipe.perTickUsage() + ");";
    }
}
