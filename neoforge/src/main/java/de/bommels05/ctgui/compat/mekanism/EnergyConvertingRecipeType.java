package de.bommels05.ctgui.compat.mekanism;

import de.bommels05.ctgui.api.SupportedRecipeType;
import de.bommels05.ctgui.api.UnsupportedRecipeException;
import de.bommels05.ctgui.api.UnsupportedViewerException;
import mekanism.api.MekanismAPI;
import mekanism.api.math.FloatingLong;
import mekanism.api.recipes.basic.BasicItemStackToEnergyRecipe;
import mekanism.api.recipes.ingredients.creator.IngredientCreatorAccess;
import mekanism.client.recipe_viewer.emi.MekanismEmiRecipeCategory;
import mekanism.client.recipe_viewer.emi.recipe.ItemStackToEnergyEmiRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.jetbrains.annotations.Nullable;

public class EnergyConvertingRecipeType extends SupportedRecipeType<BasicItemStackToEnergyRecipe> {

    public EnergyConvertingRecipeType() {
        super(new ResourceLocation(MekanismAPI.MEKANISM_MODID, "energy_conversion"));

        addAreaScrollAmountEmptyRightClick(6, 24, 17, 17, (r, am) -> {
            return new BasicItemStackToEnergyRecipe(MekanismRecipeUtils.of(convertToUnset(am)), r.getOutput(ItemStack.EMPTY));
        }, r -> {
            return convertUnset(MekanismRecipeUtils.of(r.getInput()));
        });
        addAreaScrollAmountEmptyRightClick(111, 1, 18, 60, (r, energy) -> {
            return new BasicItemStackToEnergyRecipe(r.getInput(), FloatingLong.create(Math.max(1, energy)));
        }, r -> {
            return (int) r.getOutput(ItemStack.EMPTY).doubleValue();
        }, () -> 3000, (energy, up) -> {
            return (int) (energy + (getFluidScrollAmount(up) * 2.5));
        });
    }

    @Override
    public BasicItemStackToEnergyRecipe onInitialize(@Nullable BasicItemStackToEnergyRecipe recipe) throws UnsupportedRecipeException {
        super.onInitialize(recipe);
        if (recipe == null) {
            return new BasicItemStackToEnergyRecipe(IngredientCreatorAccess.item().from(UNSET), FloatingLong.create(3000 * 2.5));
        }
        return recipe;
    }

    @Override
    public boolean isValid(BasicItemStackToEnergyRecipe recipe) {
        return !recipe.getInput().test(UNSET);
    }

    @Override
    public Object getEmiRecipe(BasicItemStackToEnergyRecipe recipe) throws UnsupportedViewerException {
        return new ItemStackToEnergyEmiRecipe((MekanismEmiRecipeCategory) getEmiCategory(new ResourceLocation(MekanismAPI.MEKANISM_MODID, "energy_conversion")), new RecipeHolder<>(nullRl(), recipe));
    }

    @Override
    public String getCraftTweakerString(BasicItemStackToEnergyRecipe recipe, String id) {
        return "<recipetype:mekanism:energy_conversion>.addRecipe(\"" + id + "\", " + getCTString(MekanismRecipeUtils.of(recipe.getInput())) + ", " + (int) recipe.getOutput(ItemStack.EMPTY).doubleValue() + ");";
    }

    @Override
    public ItemStack getMainOutput(BasicItemStackToEnergyRecipe recipe) {
        return convertUnset(MekanismRecipeUtils.of(recipe.getInput()).asStack());
    }
}
