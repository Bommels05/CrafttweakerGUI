package de.bommels05.ctgui;

import dev.emi.emi.config.EmiConfig;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

@Mod.EventBusSubscriber(modid = CraftTweakerGUI.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Config {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    private static final ModConfigSpec.BooleanValue EDIT_MODE = BUILDER.comment("Enables editing of recipes. When disabled CTGUI can still be used to display changed recipes").define("editMode", true);
    private static final ModConfigSpec.BooleanValue NO_TAG_COLLAPSING = BUILDER.push("editing").comment("Disables collapsing tags with one item into the item itself while editing recipes (In Emi)").define("noTagCollapsing", true);
    private static final ModConfigSpec.BooleanValue NO_TAG_TRANSLATIONS = BUILDER.comment("Disables tag name translation while editing recipes (In Emi)").define("noTagTranslations", true);
    private static final ModConfigSpec.BooleanValue NO_WARNING = BUILDER.comment("Disables the edit mode warning message").define("noWarning", false);
    private static final ModConfigSpec.BooleanValue SAVE_TOAST = BUILDER.comment("Shows a toast when saving recipe changes").define("saveToast", false);
    private static final ModConfigSpec.BooleanValue LIST_BUTTON = BUILDER.pop().comment("Enables a shortcut button in the pause menu to the list of changed recipes").define("listButton", true);
    private static final ModConfigSpec.BooleanValue CUSTOM_RECIPE_INDICATOR = BUILDER.comment("Enables a small indicator for all recipes by CraftTweaker (= Exported Recipe changes). This can be used to indicate changes to players after you are done editing").define("customRecipeIndicator", true);

    static final ModConfigSpec SPEC = BUILDER.build();

    public static boolean editMode;
    public static boolean noTagCollapsing;
    public static boolean noTagTranslations;
    public static boolean noWarning;
    public static boolean listButton;
    public static boolean saveToast;
    public static boolean customRecipeIndicator;

    @SubscribeEvent
    public static void onLoad(ModConfigEvent event) {
        editMode = EDIT_MODE.get();
        if (editMode) {
            noTagCollapsing = NO_TAG_COLLAPSING.get();
            noTagTranslations = NO_TAG_TRANSLATIONS.get();
            noWarning = NO_WARNING.get();
            saveToast = SAVE_TOAST.get();
            //Disabled by default but required
            EmiConfig.showRecipeDecorators = true;
            EmiConfig.writeConfig();
        }
        listButton = LIST_BUTTON.get();
        customRecipeIndicator = CUSTOM_RECIPE_INDICATOR.get();
    }

    public static void setEditMode(boolean value) {
        EDIT_MODE.set(value);
        SPEC.save();
        onLoad(null);
    }

    public static void setListButton(boolean value) {
        LIST_BUTTON.set(value);
        SPEC.save();
        onLoad(null);
    }
}
