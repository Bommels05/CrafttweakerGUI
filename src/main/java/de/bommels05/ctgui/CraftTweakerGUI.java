package de.bommels05.ctgui;

import com.mojang.logging.LogUtils;
import de.bommels05.ctgui.api.RecipeTypeManager;
import de.bommels05.ctgui.compat.minecraft.*;
import de.bommels05.ctgui.compat.minecraft.custom.TagRecipeType;
import de.bommels05.ctgui.emi.EmiViewerUtils;
import de.bommels05.ctgui.registry.RecipeSerializers;
import de.bommels05.ctgui.registry.RecipeTypes;
import de.bommels05.ctgui.screen.ChangeListScreen;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.*;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.client.ConfigScreenHandler;
import net.neoforged.neoforge.client.event.ScreenEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.level.LevelEvent;
import org.slf4j.Logger;

import java.util.Optional;

@Mod(CraftTweakerGUI.MOD_ID)
public class CraftTweakerGUI {
    public static final String MOD_ID = "ctgui";
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final ViewerUtils viewerUtils = ModList.get().isLoaded("emi") ? new EmiViewerUtils() : /*new JeiViewerUtils()*/ null;;

    public CraftTweakerGUI(IEventBus modBus, Dist dist) {
        if (dist.isClient()) {
            new ClientInit(modBus);
        } else {
            LOGGER.info("CraftTweaker GUI detected on dedicated server, not loading");
        }
    }

    public static ViewerUtils getViewerUtils() {
        return viewerUtils;
    }

}
