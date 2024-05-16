package de.bommels05.ctgui.compat.mekanism;

import de.bommels05.ctgui.api.AmountedIngredient;
import de.bommels05.ctgui.api.SupportedRecipeType;
import de.bommels05.ctgui.api.UnsupportedRecipeException;
import de.bommels05.ctgui.api.UnsupportedViewerException;
import mekanism.api.MekanismAPI;
import mekanism.api.chemical.Chemical;
import mekanism.api.chemical.ChemicalStack;
import mekanism.api.chemical.gas.GasStack;
import mekanism.api.recipes.basic.BasicChemicalCrystallizerRecipe;
import mekanism.api.recipes.ingredients.creator.IngredientCreatorAccess;
import mekanism.client.recipe_viewer.emi.MekanismEmiRecipeCategory;
import mekanism.client.recipe_viewer.emi.recipe.ChemicalCrystallizerEmiRecipe;
import mekanism.common.registries.MekanismBlocks;
import mekanism.common.registries.MekanismGases;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.jetbrains.annotations.Nullable;

public class CrystallizingRecipeType extends SupportedRecipeType<BasicChemicalCrystallizerRecipe> {

    @SuppressWarnings("unchecked")
    public <S extends ChemicalStack<T>, T extends Chemical<T>> CrystallizingRecipeType() {
        super(new ResourceLocation(MekanismAPI.MEKANISM_MODID, "crystallizing"));

        addAreaScrollAmountEmptyRightClick(124, 54, 17, 17, (r, am) -> {
            return new BasicChemicalCrystallizerRecipe(r.getInput(), convertToUnset(am.asStack()));
        }, r -> {
            return AmountedIngredient.of(convertUnset(r.getOutputRaw()));
        });
        addAreaScrollAmountEmptyRightClick(2, 1, 18, 60, (r, stack) -> {
            return new BasicChemicalCrystallizerRecipe(MekanismRecipeUtils.toIngredientKeepAmount(stack, r.getInput()), r.getOutputRaw());
        }, r -> {
            return (ChemicalAmountedIngredient<S, T>) MekanismRecipeUtils.of(r.getInput());
        }, () -> new ChemicalAmountedIngredient<>((S) new GasStack(MekanismGases.OXYGEN.get(), 100)), MekanismRecipeUtils::chemicalAmountSetter);
    }

    @Override
    public BasicChemicalCrystallizerRecipe onInitialize(@Nullable BasicChemicalCrystallizerRecipe recipe) throws UnsupportedRecipeException {
        super.onInitialize(recipe);
        if (recipe == null) {
            return new BasicChemicalCrystallizerRecipe(IngredientCreatorAccess.gas().from(MekanismGases.OXYGEN, 100), UNSET);
        }
        return recipe;
    }

    @Override
    public boolean isValid(BasicChemicalCrystallizerRecipe recipe) {
        return !ItemStack.isSameItemSameTags(recipe.getOutputRaw(), UNSET);
    }

    @Override
    public Object getEmiRecipe(BasicChemicalCrystallizerRecipe recipe) throws UnsupportedViewerException {
        return new ChemicalCrystallizerEmiRecipe((MekanismEmiRecipeCategory) getEmiCategory(new ResourceLocation(MekanismAPI.MEKANISM_MODID, "crystallizing")), new RecipeHolder<>(nullRl(), recipe));
    }

    @Override
    public String getCraftTweakerString(BasicChemicalCrystallizerRecipe recipe, String id) {
        return "<recipetype:mekanism:crystallizing>.addRecipe(\"" + id + "\", " + MekanismRecipeUtils.getCTString(recipe.getInput()) + ", " + getCTString(recipe.getOutputRaw()) + ");";
    }
}
