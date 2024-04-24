package de.bommels05.ctgui.jei;

import de.bommels05.ctgui.ChangedRecipeManager;
import de.bommels05.ctgui.CraftTweakerGUI;
import de.bommels05.ctgui.ViewerUtils;
import mezz.jei.api.ingredients.subtypes.UidContext;
import mezz.jei.api.recipe.IFocus;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.advanced.IRecipeManagerPlugin;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.runtime.IIngredientManager;
import mezz.jei.library.ingredients.IIngredientSupplier;
import mezz.jei.library.recipes.collect.RecipeMap;
import mezz.jei.library.util.IngredientSupplierHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import static de.bommels05.ctgui.jei.CTGUIJeiPlugin.RUNTIME;

public class InjectionRecipeManagerPlugin implements IRecipeManagerPlugin {


    @Override
    public <V> List<RecipeType<?>> getRecipeTypes(IFocus<V> focus) {
        RecipeMap map = new RecipeMap((o1, o2) -> o1 == o2 ? 0 : o1.getUid().compareNamespaced(o2.getUid()), RUNTIME.getIngredientManager(), focus.getRole());
        fill(map);
        return map.getRecipeTypes(getId(focus)).toList();
    }

    @Override
    public <T, V> List<T> getRecipes(IRecipeCategory<T> category, IFocus<V> focus) {
        RecipeMap map = new RecipeMap((o1, o2) -> o1 == o2 ? 0 : o1.getUid().compareNamespaced(o2.getUid()), RUNTIME.getIngredientManager(), focus.getRole());
        fill(map);
        String id = getId(focus);
        List<T> recipes = map.getRecipes(category.getRecipeType(), id);
        if (map.isCatalystForRecipeCategory(category.getRecipeType(), id)) {
            recipes.addAll(getRecipes(category));
            return recipes.stream().distinct().toList();
        }
        return recipes;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> List<T> getRecipes(IRecipeCategory<T> recipeCategory) {
        return (List<T>) getChanges().stream().filter(recipe ->
                recipe.getRecipeType().getId().equals(recipeCategory.getRecipeType().getUid())).map(InjectionRecipeManagerPlugin::toRecipeHolder).toList();
    }

    @NotNull
    private static RecipeHolder<?> toRecipeHolder(ChangedRecipeManager.ChangedRecipe<?> recipe) {
        return new RecipeHolder<>(new ResourceLocation(CraftTweakerGUI.MOD_ID, recipe.getId()), recipe.getRecipe());
    }

    private void fill(RecipeMap map) {
        for (ChangedRecipeManager.ChangedRecipe<?> change : getChanges()) {
            Optional<RecipeType<?>> type = RUNTIME.getRecipeManager().getRecipeType(change.getRecipeType().getId());
            type.ifPresent(recipeType -> addRecipe(map, recipeType, change));
        }
    }

    @SuppressWarnings("unchecked")
    private <T> void addRecipe(RecipeMap map, RecipeType<?> type, ChangedRecipeManager.ChangedRecipe<?> change) {
        T recipe = (T) toRecipeHolder(change);
        map.addRecipe((RecipeType<T>) type, recipe, IngredientSupplierHelper.getIngredientSupplier(recipe, (IRecipeCategory<T>) RUNTIME.getRecipeManager().createRecipeCategoryLookup().get().filter(
                category -> category.getRecipeType().getUid().equals(change.getRecipeType().getId())).findFirst().orElseThrow(), RUNTIME.getIngredientManager()));
    }

    private List<ChangedRecipeManager.ChangedRecipe<?>> getChanges() {
        if ((ViewerUtils<?>) CraftTweakerGUI.getViewerUtils() instanceof JeiViewerUtils utils) {
            return utils.changedRecipes;
        }
        return new ArrayList<>();
    }

    private <V> String getId(IFocus<V> focus) {
        return RUNTIME.getIngredientManager().getIngredientHelper(focus.getTypedValue().getType()).getUniqueId(focus.getTypedValue().getIngredient(), UidContext.Recipe);
    }
}
