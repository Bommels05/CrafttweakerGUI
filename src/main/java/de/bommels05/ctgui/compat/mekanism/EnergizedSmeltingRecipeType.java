package de.bommels05.ctgui.compat.mekanism;

import de.bommels05.ctgui.api.AmountedIngredient;
import de.bommels05.ctgui.api.SupportedRecipeType;
import de.bommels05.ctgui.api.UnsupportedRecipeException;
import de.bommels05.ctgui.api.UnsupportedViewerException;
import de.bommels05.ctgui.screen.RecipeEditScreen;
import mekanism.api.MekanismAPI;
import mekanism.api.recipes.basic.BasicCrushingRecipe;
import mekanism.api.recipes.basic.BasicSmeltingRecipe;
import mekanism.api.recipes.ingredients.creator.IngredientCreatorAccess;
import mekanism.client.recipe_viewer.emi.MekanismEmiRecipeCategory;
import mekanism.client.recipe_viewer.emi.recipe.ItemStackToItemStackEmiRecipe;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.jetbrains.annotations.Nullable;

public class EnergizedSmeltingRecipeType extends SupportedRecipeType<BasicSmeltingRecipe> {

    public EnergizedSmeltingRecipeType() {
        super(new ResourceLocation(MekanismAPI.MEKANISM_MODID, "smelting"));

        addAreaScrollAmountEmptyRightClick(35, 0, 17, 17, (r, am) -> {
            return new BasicSmeltingRecipe(MekanismRecipeUtils.of(convertToUnset(am)), r.getOutputRaw());
        }, r -> {
            return convertUnset(MekanismRecipeUtils.of(r.getInput()));
        });
        addAreaScrollAmountEmptyRightClick(87, 18, 17, 17, (r, am) -> {
            return new BasicSmeltingRecipe(r.getInput(), convertToUnset(am.asStack()));
        }, r -> {
            return AmountedIngredient.of(convertUnset(r.getOutputRaw()));
        });
    }

    @Override
    public BasicSmeltingRecipe onInitialize(@Nullable BasicSmeltingRecipe recipe) throws UnsupportedRecipeException {
        super.onInitialize(recipe);
        if (recipe == null) {
            return new BasicSmeltingRecipe(IngredientCreatorAccess.item().from(UNSET), UNSET);
        } else if (Minecraft.getInstance().screen instanceof RecipeEditScreen<?> screen && screen.getOriginalRecipeId().toString().startsWith("minecraft:/mekanism_generated/")) {
            throw new UnsupportedRecipeException(Component.translatable("ctgui.editing.vanilla_smelting_recipe"));
        }
        return recipe;
    }

    @Override
    public boolean isValid(BasicSmeltingRecipe recipe) {
        return !recipe.getInput().test(UNSET) && !ItemStack.isSameItemSameTags(recipe.getOutputRaw(), UNSET);
    }

    @Override
    public Object getEmiRecipe(BasicSmeltingRecipe recipe) throws UnsupportedViewerException {
        return new ItemStackToItemStackEmiRecipe((MekanismEmiRecipeCategory) getEmiCategory(new ResourceLocation(MekanismAPI.MEKANISM_MODID, "smelting")), new RecipeHolder<>(nullRl(), recipe));
    }

    @Override
    public String getCraftTweakerString(BasicSmeltingRecipe recipe, String id) {
        return "<recipetype:mekanism:smelting>.addRecipe(\"" + id + "\", " + getCTString(MekanismRecipeUtils.of(recipe.getInput())) + ", " + getCTString(recipe.getOutputRaw()) + ");";
    }
}
