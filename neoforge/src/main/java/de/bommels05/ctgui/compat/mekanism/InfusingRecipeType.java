package de.bommels05.ctgui.compat.mekanism;

import de.bommels05.ctgui.api.AmountedIngredient;
import de.bommels05.ctgui.api.SupportedRecipeType;
import de.bommels05.ctgui.api.UnsupportedRecipeException;
import de.bommels05.ctgui.api.UnsupportedViewerException;
import de.bommels05.ctgui.api.option.BooleanRecipeOption;
import mekanism.api.MekanismAPI;
import mekanism.api.chemical.ChemicalStack;
import mekanism.api.recipes.basic.BasicMetallurgicInfuserRecipe;
import mekanism.api.recipes.ingredients.creator.IngredientCreatorAccess;
import mekanism.client.recipe_viewer.emi.MekanismEmiRecipeCategory;
import mekanism.client.recipe_viewer.emi.recipe.MetallurgicInfuserEmiRecipe;
import mekanism.common.registries.MekanismChemicals;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.jetbrains.annotations.Nullable;

public class InfusingRecipeType extends SupportedRecipeType<BasicMetallurgicInfuserRecipe> {

    private final BooleanRecipeOption<BasicMetallurgicInfuserRecipe> perTickUsage = new BooleanRecipeOption<>(Component.translatable("ctgui.editing.options.per_tick_usage"), Component.translatable("ctgui.editing.options.per_tick_usage_chemical"));

    public InfusingRecipeType() {
        super(ResourceLocation.fromNamespaceAndPath(MekanismAPI.MEKANISM_MODID, "metallurgic_infusing"));

        addAreaScrollAmountEmptyRightClick(45, 26, 17, 17, (r, am) -> {
            return new BasicMetallurgicInfuserRecipe(MekanismRecipeUtils.of(convertToUnset(am)), r.getChemicalInput(), r.getOutputRaw(), r.perTickUsage());
        }, r -> {
            return convertUnset(MekanismRecipeUtils.of(r.getItemInput()));
        });
        addAreaScrollAmountEmptyRightClick(103, 26, 17, 17, (r, am) -> {
            return new BasicMetallurgicInfuserRecipe(r.getItemInput(), r.getChemicalInput(), convertToUnset(am.asStack()), r.perTickUsage());
        }, r -> {
            return AmountedIngredient.of(convertUnset(r.getOutputRaw()));
        });
        addAreaScrollAmountEmptyRightClick(1, -2, 6, 54, (r, stack) -> {
            return new BasicMetallurgicInfuserRecipe(r.getItemInput(), MekanismRecipeUtils.toIngredientKeepAmount(stack, r.getChemicalInput()), r.getOutputRaw(), r.perTickUsage());
        }, r -> {
            return MekanismRecipeUtils.of(r.getChemicalInput());
        }, () -> new ChemicalAmountedIngredient(new ChemicalStack(MekanismChemicals.REDSTONE.get(), 10)), (stack, up) ->
                perTickUsage.get() ? MekanismRecipeUtils.limitedChemicalAmountSetter(stack, up) :
                        MekanismRecipeUtils.chemicalAmountSetter(stack, up, 1, 10, 100));
        addOption(perTickUsage, (r, value) -> {
            return new BasicMetallurgicInfuserRecipe(r.getItemInput(), r.getChemicalInput(), r.getOutputRaw(), value);
        });
    }

    @Override
    public BasicMetallurgicInfuserRecipe onInitialize(@Nullable BasicMetallurgicInfuserRecipe recipe) throws UnsupportedRecipeException {
        super.onInitialize(recipe);
        if (recipe == null) {
            return new BasicMetallurgicInfuserRecipe(IngredientCreatorAccess.item().from(UNSET), IngredientCreatorAccess.chemicalStack().from(MekanismChemicals.REDSTONE, 10), UNSET, false);
        }
        perTickUsage.set(recipe.perTickUsage());
        return recipe;
    }

    @Override
    public boolean isValid(BasicMetallurgicInfuserRecipe recipe) {
        return !recipe.getItemInput().test(UNSET) && !ItemStack.isSameItemSameComponents(recipe.getOutputRaw(), UNSET);
    }

    @Override
    public Object getEmiRecipe(BasicMetallurgicInfuserRecipe recipe) throws UnsupportedViewerException {
        return new MetallurgicInfuserEmiRecipe((MekanismEmiRecipeCategory) getEmiCategory(ResourceLocation.fromNamespaceAndPath(MekanismAPI.MEKANISM_MODID, "metallurgic_infusing")), new RecipeHolder<>(nullRl(), recipe));
    }

    @Override
    public String getCraftTweakerString(BasicMetallurgicInfuserRecipe recipe, String id) {
        return "<recipetype:mekanism:metallurgic_infusing>.addRecipe(\"" + id + "\", " + getCTString(MekanismRecipeUtils.of(recipe.getItemInput())) + ", " + MekanismRecipeUtils.getCTString(recipe.getChemicalInput()) + ", " + getCTString(recipe.getOutputRaw()) + ", " + recipe.perTickUsage() + ");";
    }
}
