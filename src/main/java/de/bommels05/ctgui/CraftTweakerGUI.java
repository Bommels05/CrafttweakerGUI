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
            NeoForge.EVENT_BUS.register(this);
            RecipeTypes.RECIPE_TYPES.register(modBus);
            RecipeSerializers.RECIPE_SERIALIZERS.register(modBus);

            ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SPEC);
            ModLoadingContext.get().registerExtensionPoint(ConfigScreenHandler.ConfigScreenFactory.class,
                    () -> new ConfigScreenHandler.ConfigScreenFactory((mc, screen) -> new ChangeListScreen()));

            /*if (!ModList.get().isLoaded("emi") && !ModList.get().isLoaded("jei")) {
                //ModLoader.get().addWarning(new ModLoadingWarning(ModLoadingContext.get().getActiveContainer().getModInfo(), ModLoadingStage.CONSTRUCT, "Either Emi or Jei is required for Crafttweaker GUI to work"));
                throw new IllegalStateException("Either Emi or Jei is required for Crafttweaker GUI to work");
                return;
            }*/

            RecipeTypeManager.addType(new CraftingRecipeType());
            RecipeTypeManager.addType(new SmeltingRecipeType());
            RecipeTypeManager.addType(new BlastingRecipeType());
            RecipeTypeManager.addType(new SmokingRecipeType());
            RecipeTypeManager.addType(new CampfireCookingRecipeType());
            RecipeTypeManager.addType(new StoneCuttingRecipeType());
            RecipeTypeManager.addType(new SmithingRecipeType());
            RecipeTypeManager.addType(new TagRecipeType());
        } else {
            LOGGER.info("CraftTweaker GUI detected on dedicated server, not loading");
        }
    }

    @SubscribeEvent
    public void onSave(LevelEvent.Save event) {
        //This is a server event but we are on the physical client
        if (Config.editMode) {
            ChangedRecipeManager.save();
        }
    }

    @SubscribeEvent
    public void onJoin(PlayerEvent.PlayerLoggedInEvent event) {
        if (Config.editMode && !Config.noWarning) {
            event.getEntity().sendSystemMessage(Component.translatable("ctgui.editing.options_warning").withStyle(ChatFormatting.GOLD));
        }
    }

    @SubscribeEvent
    public void onInitScreen(ScreenEvent.Init.Post event) {
        if (event.getScreen() instanceof PauseScreen && Config.listButton) {
            Optional<Button> button = event.getListenersList().stream().filter(listener -> listener instanceof Button b && b.getMessage().getContents() instanceof TranslatableContents).map(b -> (Button) b).filter(b -> ((TranslatableContents) b.getMessage().getContents()).getKey().equals("gui.advancements")).findFirst();
            if (button.isPresent()) {
                Button b = button.get();
                event.addListener(Button.builder(Component.literal("CT"), b2 -> Minecraft.getInstance().setScreen(new ChangeListScreen())).bounds(b.getX() - 24, b.getY(), 20, 20).build());
            }
        }
    }

    public static ViewerUtils getViewerUtils() {
        return viewerUtils;
    }

}
