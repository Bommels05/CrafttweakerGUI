package de.bommels05.ctgui;

import com.blamejared.crafttweaker.api.CraftTweakerConstants;
import de.bommels05.ctgui.api.RecipeTypeManager;
import de.bommels05.ctgui.compat.minecraft.*;
import de.bommels05.ctgui.compat.minecraft.custom.TagRecipeType;
import net.minecraft.resources.ResourceLocation;

public class CraftTweakerGUI {
    public static final String MOD_ID = "ctgui";
    protected static ViewerUtils<?> viewerUtils;
    protected static LoaderUtils loaderUtils;

    @SuppressWarnings("unchecked")
    public static <T> ViewerUtils<T> getViewerUtils() {
        return (ViewerUtils<T>) viewerUtils;
    }

    public static LoaderUtils getLoaderUtils() {
        return loaderUtils;
    }

    public static boolean isJeiActive() {
        return loaderUtils.isModLoaded("jei") && !loaderUtils.isModLoaded("emi");
    }

    public static <T> boolean shouldShowEditButton(ResourceLocation categoryId, ResourceLocation recipeId, T viewerRecipe) {
        return Config.editMode && RecipeTypeManager.isTypeSupported(categoryId) &&
                ChangedRecipeManager.getAffectingChange(recipeId) == null
                && !getViewerUtils().isCustomTagRecipe(viewerRecipe)
                && (recipeId != null && !recipeId.getNamespace().equals(CraftTweakerConstants.MOD_ID)) && !recipeId.getNamespace().equals(CraftTweakerGUI.MOD_ID);
    }

    public static void initVanillaRecipeTypes() {
        RecipeTypeManager.addType(new CraftingRecipeType());
        RecipeTypeManager.addType(new SmeltingRecipeType());
        RecipeTypeManager.addType(new BlastingRecipeType());
        RecipeTypeManager.addType(new SmokingRecipeType());
        RecipeTypeManager.addType(new CampfireCookingRecipeType());
        RecipeTypeManager.addType(new StoneCuttingRecipeType());
        RecipeTypeManager.addType(new SmithingRecipeType());
        RecipeTypeManager.addType(new TagRecipeType());
    }

}
