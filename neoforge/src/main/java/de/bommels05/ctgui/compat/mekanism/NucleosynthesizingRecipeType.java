package de.bommels05.ctgui.compat.mekanism;

import de.bommels05.ctgui.CraftTweakerGUI;
import de.bommels05.ctgui.api.AmountedIngredient;
import de.bommels05.ctgui.api.SupportedRecipeType;
import de.bommels05.ctgui.api.UnsupportedRecipeException;
import de.bommels05.ctgui.api.UnsupportedViewerException;
import de.bommels05.ctgui.api.option.BooleanRecipeOption;
import de.bommels05.ctgui.api.option.IntegerRecipeOption;
import mekanism.api.MekanismAPI;
import mekanism.api.chemical.ChemicalStack;
import mekanism.api.recipes.basic.BasicNucleosynthesizingRecipe;
import mekanism.api.recipes.ingredients.creator.IngredientCreatorAccess;
import mekanism.client.recipe_viewer.emi.MekanismEmiRecipeCategory;
import mekanism.client.recipe_viewer.emi.recipe.NucleosynthesizingEmiRecipe;
import mekanism.common.registries.MekanismChemicals;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.jetbrains.annotations.Nullable;

public class NucleosynthesizingRecipeType extends SupportedRecipeType<BasicNucleosynthesizingRecipe> {

    private final IntegerRecipeOption<BasicNucleosynthesizingRecipe> duration = new IntegerRecipeOption<>(Component.translatable("ctgui.editing.options.duration"), 1);
    private final BooleanRecipeOption<BasicNucleosynthesizingRecipe> perTickUsage = new BooleanRecipeOption<>(Component.translatable("ctgui.editing.options.per_tick_usage"), Component.translatable("ctgui.editing.options.per_tick_usage_chemical"));

    public NucleosynthesizingRecipeType() {
        super(CraftTweakerGUI.rl(MekanismAPI.MEKANISM_MODID, "nucleosynthesizing"));


        addAreaScrollAmountEmptyRightClick(20, 22, 17, 17, (r, am) -> {
            return new BasicNucleosynthesizingRecipe(MekanismRecipeUtils.of(convertToUnset(am)), r.getChemicalInput(), r.getOutputRaw(), r.getDuration(), r.perTickUsage());
        }, r -> {
            return convertUnset(MekanismRecipeUtils.of(r.getItemInput()));
        });
        addAreaScrollAmountEmptyRightClick(146, 22, 17, 17, (r, am) -> {
            return new BasicNucleosynthesizingRecipe(r.getItemInput(), r.getChemicalInput(), convertToUnset(am.asStack()), r.getDuration(), r.perTickUsage());
        }, r -> {
            return AmountedIngredient.of(convertUnset(r.getOutputRaw()));
        });
        addAreaScrollAmountEmptyRightClick(-1, 0, 18, 60, (r, stack) -> {
            return new BasicNucleosynthesizingRecipe(r.getItemInput(), MekanismRecipeUtils.toIngredientKeepAmount(stack, r.getChemicalInput()), r.getOutputRaw(), r.getDuration(), r.perTickUsage());
        }, r -> {
            return MekanismRecipeUtils.of(r.getChemicalInput());
        }, () -> new ChemicalAmountedIngredient(new ChemicalStack(MekanismChemicals.ANTIMATTER, 2)), (stack, up) ->
                perTickUsage.get() ? MekanismRecipeUtils.limitedChemicalAmountSetter(stack, up) :
                        MekanismRecipeUtils.chemicalAmountSetter(stack, up, 1, 10));

        addOption(duration, (r, duration) -> {
            return new BasicNucleosynthesizingRecipe(r.getItemInput(), r.getChemicalInput(), r.getOutputRaw(), duration, r.perTickUsage());
        });
        addOption(perTickUsage, (r, value) -> {
            return new BasicNucleosynthesizingRecipe(r.getItemInput(), r.getChemicalInput(), r.getOutputRaw(), r.getDuration(), value);
        });
    }

    @Override
    public BasicNucleosynthesizingRecipe onInitialize(@Nullable BasicNucleosynthesizingRecipe recipe) throws UnsupportedRecipeException {
        super.onInitialize(recipe);
        if (recipe == null) {
            duration.set(500);
            return new BasicNucleosynthesizingRecipe(IngredientCreatorAccess.item().from(UNSET),
                    IngredientCreatorAccess.chemicalStack().fromHolder(MekanismChemicals.ANTIMATTER, 2), UNSET, 500, false);
        }
        duration.set(recipe.getDuration());
        perTickUsage.set(recipe.perTickUsage());
        return recipe;
    }

    @Override
    public boolean isValid(BasicNucleosynthesizingRecipe recipe) {
        return !recipe.getItemInput().test(UNSET) && !ItemStack.isSameItemSameComponents(recipe.getOutputRaw(), UNSET);
    }

    @Override
    public Object getEmiRecipe(BasicNucleosynthesizingRecipe recipe) throws UnsupportedViewerException {
        return new NucleosynthesizingEmiRecipe((MekanismEmiRecipeCategory) getEmiCategory(CraftTweakerGUI.rl(MekanismAPI.MEKANISM_MODID, "nucleosynthesizing")), new RecipeHolder<>(nullRl(), recipe));
    }

    @Override
    public String getCraftTweakerString(BasicNucleosynthesizingRecipe recipe, String id) {
        return "<recipetype:mekanism:nucleosynthesizing>.addRecipe(\"" + id + "\", " + getCTString(MekanismRecipeUtils.of(recipe.getItemInput())) + ", " + MekanismRecipeUtils.getCTString(recipe.getChemicalInput()) + ", " + getCTString(recipe.getOutputRaw()) + ", " + recipe.getDuration() + ");";
    }
}
