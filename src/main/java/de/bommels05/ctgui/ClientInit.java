package de.bommels05.ctgui;

import de.bommels05.ctgui.api.RecipeTypeManager;
import de.bommels05.ctgui.compat.mekanism.*;
import de.bommels05.ctgui.compat.minecraft.*;
import de.bommels05.ctgui.compat.minecraft.custom.TagRecipeType;
import de.bommels05.ctgui.registry.RecipeSerializers;
import de.bommels05.ctgui.registry.RecipeTypes;
import de.bommels05.ctgui.screen.ChangeListScreen;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.*;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.client.ConfigScreenHandler;
import net.neoforged.neoforge.client.event.ScreenEvent;
import net.neoforged.neoforge.client.loading.ClientModLoader;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.neoforgespi.language.IModInfo;
import net.neoforged.neoforgespi.language.MavenVersionAdapter;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ClientInit {

    public ClientInit(IEventBus modBus) {
        NeoForge.EVENT_BUS.register(this);
        RecipeTypes.RECIPE_TYPES.register(modBus);
        RecipeSerializers.RECIPE_SERIALIZERS.register(modBus);

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SPEC);
        ModLoadingContext.get().registerExtensionPoint(ConfigScreenHandler.ConfigScreenFactory.class,
                () -> new ConfigScreenHandler.ConfigScreenFactory((mc, screen) -> new ChangeListScreen()));

        if (!ModList.get().isLoaded("emi") && !ModList.get().isLoaded("jei")) {
            try {
                Field field = ClientModLoader.class.getDeclaredField("error");
                field.setAccessible(true);
                field.set(null, new LoadingFailedException(List.of(new ModLoadingException(
                        ModLoadingContext.get().getActiveContainer().getModInfo(), ModLoadingStage.VALIDATE, "fml.modloading.missingdependency", null, "jei or emi",
                        CraftTweakerGUI.MOD_ID, MavenVersionAdapter.createFromVersionSpec("[17.3.0.49,)[1.1.2+1.20.4+neoforge,)"),
                        new DefaultArtifactVersion("null"), Optional.empty()))));
            } catch (Throwable ignored) {}
            throw new IllegalStateException("Either Emi or Jei is required for Crafttweaker GUI to work");
        }

        RecipeTypeManager.addType(new CraftingRecipeType());
        RecipeTypeManager.addType(new SmeltingRecipeType());
        RecipeTypeManager.addType(new BlastingRecipeType());
        RecipeTypeManager.addType(new SmokingRecipeType());
        RecipeTypeManager.addType(new CampfireCookingRecipeType());
        RecipeTypeManager.addType(new StoneCuttingRecipeType());
        RecipeTypeManager.addType(new SmithingRecipeType());
        RecipeTypeManager.addType(new TagRecipeType());
        if (ModList.get().isLoaded("mekanism")) {
            RecipeTypeManager.addType(new CrushingRecipeType());
            RecipeTypeManager.addType(new EnrichingRecipeType());
            RecipeTypeManager.addType(new EnergizedSmeltingRecipeType());
            RecipeTypeManager.addType(new SawingRecipeType());
            RecipeTypeManager.addType(new CombiningRecipeType());
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

}
