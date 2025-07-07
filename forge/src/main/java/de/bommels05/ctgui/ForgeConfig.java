package de.bommels05.ctgui;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;

@Mod.EventBusSubscriber(modid = CraftTweakerGUI.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ForgeConfig {
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    private static final ForgeConfigSpec.BooleanValue EDIT_MODE = BUILDER.comment("Enables editing of recipes. When disabled CTGUI can still be used to display changed recipes").define("editMode", true);
    private static final ForgeConfigSpec.BooleanValue NO_TAG_COLLAPSING = BUILDER.push("editing").comment("Disables collapsing tags with one item into the item itself while editing recipes (In Emi)").define("noTagCollapsing", true);
    private static final ForgeConfigSpec.BooleanValue NO_TAG_TRANSLATIONS = BUILDER.comment("Disables tag name translation while editing recipes (In Emi)").define("noTagTranslations", true);
    private static final ForgeConfigSpec.BooleanValue SHOW_TAGS_EVERYWHERE = BUILDER.comment("Also disables tag collapsing and translations outside the recipe editing screen if their options are enabled").define("showTagsEverywhere", false);
    private static final ForgeConfigSpec.BooleanValue NO_WARNING = BUILDER.comment("Disables the edit mode warning message").define("noWarning", true);
    private static final ForgeConfigSpec.BooleanValue SAVE_TOAST = BUILDER.comment("Shows a toast when saving recipe changes").define("saveToast", false);
    private static final ForgeConfigSpec.BooleanValue LIST_BUTTON = BUILDER.pop().comment("Enables a shortcut button in the pause menu to the list of changed recipes").define("listButton", true);
    private static final ForgeConfigSpec.BooleanValue CUSTOM_RECIPE_INDICATOR = BUILDER.comment("Enables a small indicator for all recipes by CraftTweaker (= Exported Recipe changes). This can be used to indicate changes to players after you are done editing").define("customRecipeIndicator", true);

    static final ForgeConfigSpec SPEC = BUILDER.build();

    @SubscribeEvent
    public static void onLoad(ModConfigEvent event) {
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
        onLoad(null);
    }

    public static void setListButton(boolean value) {
        LIST_BUTTON.set(value);
        SPEC.save();
        onLoad(null);
    }
}
