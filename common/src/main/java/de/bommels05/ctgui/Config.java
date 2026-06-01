package de.bommels05.ctgui;

public class Config {

    public static boolean editMode;
    public static boolean noTagCollapsing;
    public static boolean noTagTranslations;
    public static boolean noWarning;
    public static boolean listButton;
    public static boolean saveToast;
    public static boolean customRecipeIndicator;
    public static boolean showTagsEverywhere;
    public static boolean acknowledgedMultiplayer;

    public static void setEditMode(boolean value) {
        CraftTweakerGUI.getLoaderUtils().setEditMode(value);
    }

    public static void setListButton(boolean value) {
        CraftTweakerGUI.getLoaderUtils().setListButton(value);
    }
}
