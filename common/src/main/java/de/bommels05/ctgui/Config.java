package de.bommels05.ctgui;

import dev.emi.emi.config.EmiConfig;

public class Config {

    public static boolean editMode;
    public static boolean noTagCollapsing;
    public static boolean noTagTranslations;
    public static boolean noWarning;
    public static boolean listButton;
    public static boolean saveToast;
    public static boolean customRecipeIndicator;
    public static boolean showTagsEverywhere;

    public static void afterLoad() {
        if (editMode && CraftTweakerGUI.getLoaderUtils().isModLoaded("emi")) {
            //Disabled by default but required
            EmiConfig.showRecipeDecorators = true;
            EmiConfig.writeConfig();
        }
    }

    public static void setEditMode(boolean value) {
        CraftTweakerGUI.getLoaderUtils().setEditMode(value);
    }

    public static void setListButton(boolean value) {
        CraftTweakerGUI.getLoaderUtils().setListButton(value);
    }
}
