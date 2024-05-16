package de.bommels05.ctgui;

import com.blamejared.crafttweaker.api.CraftTweakerConstants;
import com.mojang.logging.LogUtils;
import de.bommels05.ctgui.api.RecipeTypeManager;
import de.bommels05.ctgui.emi.EmiViewerUtils;
import de.bommels05.ctgui.jei.JeiViewerUtils;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.*;
import net.neoforged.fml.common.Mod;
import org.slf4j.Logger;

@Mod(CraftTweakerGUI.MOD_ID)
public class CraftTweakerGUI {
    public static final String MOD_ID = "ctgui";
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final ViewerUtils<?> viewerUtils = ModList.get().isLoaded("emi") ? new EmiViewerUtils() : new JeiViewerUtils();

    public CraftTweakerGUI(IEventBus modBus, Dist dist) {
        if (dist.isClient()) {
            new ClientInit(modBus);
        } else {
            LOGGER.info("CraftTweaker GUI detected on dedicated server, not loading");
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> ViewerUtils<T> getViewerUtils() {
        return (ViewerUtils<T>) viewerUtils;
    }

    public static boolean isJeiActive() {
        return ModList.get().isLoaded("jei") && !ModList.get().isLoaded("emi");
    }

    public static <T> boolean shouldShowEditButton(ResourceLocation categoryId, ResourceLocation recipeId, T viewerRecipe) {
        return Config.editMode && RecipeTypeManager.isTypeSupported(categoryId) &&
                ChangedRecipeManager.getAffectingChange(recipeId) == null
                && !getViewerUtils().isCustomTagRecipe(viewerRecipe)
                && (recipeId != null && !recipeId.getNamespace().equals(CraftTweakerConstants.MOD_ID)) && !recipeId.getNamespace().equals(CraftTweakerGUI.MOD_ID);
    }

}
