package de.bommels05.ctgui;

import net.neoforged.neoforge.common.ModConfigSpec;

public class FabricConfig {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    private static final ModConfigSpec.BooleanValue EDIT_MODE = BUILDER.comment("Enables editing of recipes. When disabled CTGUI can still be used to display changed recipes").define("editMode", true);
    private static final ModConfigSpec.BooleanValue NO_TAG_COLLAPSING = BUILDER.push("editing").comment("Disables collapsing tags with one item into the item itself while editing recipes (In Emi)").define("noTagCollapsing", true);
    private static final ModConfigSpec.BooleanValue NO_TAG_TRANSLATIONS = BUILDER.comment("Disables tag name translation while editing recipes (In Emi)").define("noTagTranslations", true);
    private static final ModConfigSpec.BooleanValue SHOW_TAGS_EVERYWHERE = BUILDER.comment("Also disables tag collapsing and translations outside the recipe editing screen if their options are enabled").define("showTagsEverywhere", false);
    private static final ModConfigSpec.BooleanValue NO_WARNING = BUILDER.comment("Disables the edit mode warning message").define("noWarning", true);
    private static final ModConfigSpec.BooleanValue SAVE_TOAST = BUILDER.comment("Shows a toast when saving recipe changes").define("saveToast", false);
    private static final ModConfigSpec.BooleanValue LIST_BUTTON = BUILDER.pop().comment("Enables a shortcut button in the pause menu to the list of changed recipes").define("listButton", true);
    private static final ModConfigSpec.BooleanValue CUSTOM_RECIPE_INDICATOR = BUILDER.comment("Enables a small indicator for all recipes by CraftTweaker (= Exported Recipe changes). This can be used to indicate changes to players after you are done editing").define("customRecipeIndicator", true);

    static final ModConfigSpec SPEC = BUILDER.build();

    public static void onLoad() {
        Config.editMode = EDIT_MODE.get();
        if (Config.editMode) {
            Config.noTagCollapsing = NO_TAG_COLLAPSING.get();
            Config.noTagTranslations = NO_TAG_TRANSLATIONS.get();
            Config.noWarning = NO_WARNING.get();
            Config.saveToast = SAVE_TOAST.get();
            Config.showTagsEverywhere = SHOW_TAGS_EVERYWHERE.get();
        }
        Config.listButton = LIST_BUTTON.get();
        Config.customRecipeIndicator = CUSTOM_RECIPE_INDICATOR.get();
        Config.afterLoad();
    }

    public static void setEditMode(boolean value) {
        EDIT_MODE.set(value);
        SPEC.save();
        onLoad();
    }

    public static void setListButton(boolean value) {
        LIST_BUTTON.set(value);
        SPEC.save();
        onLoad();
    }
}
