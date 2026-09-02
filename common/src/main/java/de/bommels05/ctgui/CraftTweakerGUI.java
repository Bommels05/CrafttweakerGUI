package de.bommels05.ctgui;

import com.blamejared.crafttweaker.api.CraftTweakerConstants;
import de.bommels05.ctgui.api.RecipeTypeManager;
import de.bommels05.ctgui.compat.alloyforgery.AlloyForgeRecipeType;
import de.bommels05.ctgui.compat.minecraft.*;
import de.bommels05.ctgui.compat.minecraft.custom.CompostingRecipeType;
import de.bommels05.ctgui.compat.minecraft.custom.FuelRecipeType;
import de.bommels05.ctgui.compat.minecraft.custom.InfoRecipeType;
import de.bommels05.ctgui.compat.minecraft.custom.TagRecipeType;
import dev.emi.emi.config.EmiConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

import java.util.HashMap;
import java.util.Map;

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
        return Config.editMode && RecipeTypeManager.isTypeSupported(categoryId)
                && RecipeTypeManager.getType(categoryId).supportsEditing()
                && ChangedRecipeManager.getAffectingChange(recipeId) == null
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
        if (!isJeiActive()) {
            RecipeTypeManager.addType(new FuelRecipeType());
            RecipeTypeManager.addType(new CompostingRecipeType());
        }
        if (loaderUtils.isModLoaded("jeitweaker")) { //Emi also shows the jei info recipes
            RecipeTypeManager.addType(new InfoRecipeType());
        }
        if (loaderUtils.isModLoaded("alloy_forgery")) {
            RecipeTypeManager.addType(new AlloyForgeRecipeType());
        }
    }

    public static void handleJoin(Player player) {
        if (Config.editMode && CraftTweakerGUI.getLoaderUtils().isModLoaded("emi")) {
            //Disabled by default but required
            EmiConfig.showRecipeDecorators = true;
            EmiConfig.writeConfig();
        }
        if (Config.editMode && !Config.noWarning) {
            player.sendSystemMessage(Component.translatable("ctgui.editing.options_warning").withStyle(ChatFormatting.GOLD));
        }
    }

    public static <K, V> Map<K, V> setMapEntry(Map<K, V> map, int i, K key, V value, boolean remove) {
        Map<K, V> newMap = new HashMap<>();
        int i2 = 0;
        for (Map.Entry<K, V> entry : map.entrySet()) {
            if (i2 == i) {
                if (!remove) {
                    newMap.put(key == null ? entry.getKey() : key, value == null ? entry.getValue() : value);
                }
            } else {
                newMap.put(entry.getKey(), entry.getValue());
            }
            i2++;
        }
        if (i >= map.size() && key != null && value != null && !remove) {
            newMap.put(key, value);
        }
        return newMap;
    }

    //For better 1.20.1 porting
    public static ResourceLocation rl(String s) {
        return ResourceLocation.parse(s);
    }

    public static ResourceLocation rl(String namespace, String path) {
        return ResourceLocation.fromNamespaceAndPath(namespace, path);
    }

}
