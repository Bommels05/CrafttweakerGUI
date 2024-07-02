package de.bommels05.ctgui;

import de.bommels05.ctgui.compat.minecraft.custom.TagRecipe;
import de.bommels05.ctgui.compat.minecraft.custom.TagRecipeSerializer;
import de.bommels05.ctgui.emi.EmiViewerUtils;
import de.bommels05.ctgui.jei.JeiViewerUtils;
import de.bommels05.ctgui.screen.ChangeListScreen;
import fuzs.forgeconfigapiport.fabric.api.neoforge.v4.NeoForgeConfigRegistry;
import fuzs.forgeconfigapiport.fabric.api.neoforge.v4.NeoForgeModConfigEvents;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.fml.config.ModConfig;

public class ClientInit implements ClientModInitializer {

    public static RecipeType<TagRecipe> tagRecipeType;
    public static RecipeSerializer<TagRecipe> tagRecipeSerializer;

    @Override
    public void onInitializeClient() {
        CraftTweakerGUI.viewerUtils = FabricLoader.getInstance().isModLoaded("emi") ? new EmiViewerUtils() : new JeiViewerUtils();
        CraftTweakerGUI.loaderUtils = new FabricLoaderUtils();

        String id = new ResourceLocation(CraftTweakerGUI.MOD_ID, "tag").toString();
        tagRecipeType = Registry.register(BuiltInRegistries.RECIPE_TYPE, id, new RecipeType<>() {
            public String toString() {
                return id;
            }
        });
        tagRecipeSerializer = Registry.register(BuiltInRegistries.RECIPE_SERIALIZER, new ResourceLocation(CraftTweakerGUI.MOD_ID, "tag"), new TagRecipeSerializer());

        NeoForgeModConfigEvents.reloading(CraftTweakerGUI.MOD_ID).register(config -> FabricConfig.onLoad());
        NeoForgeModConfigEvents.loading(CraftTweakerGUI.MOD_ID).register(config -> FabricConfig.onLoad());
        NeoForgeConfigRegistry.INSTANCE.register(CraftTweakerGUI.MOD_ID, ModConfig.Type.COMMON, FabricConfig.SPEC);

        if (!FabricLoader.getInstance().isModLoaded("emi") && !FabricLoader.getInstance().isModLoaded("jei")) {
            throw new IllegalStateException("Either Emi or Jei is required for CraftTweaker GUI to work");
        }

        CraftTweakerGUI.initVanillaRecipeTypes();

        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            if (Config.editMode && !Config.noWarning) {
                handler.getPlayer().sendSystemMessage(Component.translatable("ctgui.editing.options_warning").withStyle(ChatFormatting.GOLD));
            }
        });
    }
}
