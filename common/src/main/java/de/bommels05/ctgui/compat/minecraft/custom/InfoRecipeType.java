package de.bommels05.ctgui.compat.minecraft.custom;

import de.bommels05.ctgui.CraftTweakerGUI;
import de.bommels05.ctgui.api.AmountedIngredient;
import de.bommels05.ctgui.api.SupportedRecipeType;
import de.bommels05.ctgui.api.UnsupportedRecipeException;
import de.bommels05.ctgui.api.UnsupportedViewerException;
import de.bommels05.ctgui.api.option.RecipeIdFieldRecipeOption;
import de.bommels05.ctgui.emi.EmiViewerUtils;
import de.bommels05.ctgui.mixin.EmiCompostingRecipeAccessor;
import de.bommels05.ctgui.mixin.EmiInfoRecipeAccessor;
import dev.emi.emi.api.recipe.EmiInfoRecipe;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.stack.EmiIngredient;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Function;

public class InfoRecipeType extends SupportedRecipeType<InfoRecipe> {
    RecipeIdFieldRecipeOption<InfoRecipe> text = new RecipeIdFieldRecipeOption<>(Component.translatable("ctgui.editing.options.info_text"), s -> true);

    public InfoRecipeType() {
        super(CraftTweakerGUI.rl(CraftTweakerGUI.isJeiActive() ? "minecraft:info" : "emi:info"));

        addAreaEmptyRightClick(0, 0, 17, 17, (r, am) -> {
            return new InfoRecipe(am.ensureAmount(1, 1).ingredient(), r.getText());
        }, r -> {
            return new AmountedIngredient(r.getIngredient(), 1);
        });
        addOption(text, (r, value) -> {
            return new InfoRecipe(r.getIngredient(), value);
        });
    }

    @Override
    public InfoRecipe onInitialize(@Nullable InfoRecipe recipe) throws UnsupportedRecipeException {
        super.onInitialize(recipe);
        if (recipe == null) {
            text.set("Info Text...");
            return new InfoRecipe(Ingredient.EMPTY, "Info Text...");
        } else if (recipe.getText() == null) {
            throw new UnsupportedRecipeException(Component.translatable("ctgui.editing.multipage_info_recipe")); //null means currently unsupported multi-page info
        }
        text.set(recipe.getText());
        return null;
    }

    @Override
    public boolean isValid(InfoRecipe recipe) {
        return !recipe.getIngredient().isEmpty() && !recipe.getText().isEmpty();
    }

    @Override
    public Object getEmiRecipe(InfoRecipe recipe) throws UnsupportedViewerException {
        return new EmiInfoRecipe(List.of(EmiIngredient.of(recipe.getIngredient())), List.of(Component.literal(recipe.getText())), nullRl());
    }

    @Override
    public Function<EmiRecipe, InfoRecipe> getAlternativeEmiRecipeGetter() {
        return recipe -> recipe instanceof EmiInfoRecipeAccessor r ? new InfoRecipe(EmiViewerUtils.getElseEmpty(EmiIngredient.of(r.getStacks())), r.getText().size() == 1 ? r.getText().get(0).getString() : null) : null;
    }

    @Override
    public String getCraftTweakerString(InfoRecipe recipe, String id) {
        return "Jei.addIngredientInformation(" + getCTString(recipe.getIngredient()) + ", Component.literal(\"" + recipe.getText() + "\"));";
    }

    @Override
    public String getCraftTweakerImportsString() {
        return "import mods.jeitweaker.Jei;";
    }

    @Override
    public boolean supportsEditing() {
        return false; //Not supported by JeiTweaker
    }

    @Override
    public boolean needsRecipeId() {
        return false;
    }
}
