package de.bommels05.ctgui;

import de.bommels05.ctgui.api.RecipeTypeManager;
import de.bommels05.ctgui.compat.mekanism.*;
import de.bommels05.ctgui.compat.minecraft.BrewingRecipeType;
import de.bommels05.ctgui.emi.EmiViewerUtils;
import de.bommels05.ctgui.jei.JeiViewerUtils;
import de.bommels05.ctgui.registry.RecipeSerializers;
import de.bommels05.ctgui.registry.RecipeTypes;
import de.bommels05.ctgui.screen.ChangeListScreen;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.client.loading.ClientModLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.ModLoadingException;
import net.minecraftforge.fml.ModLoadingStage;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.forgespi.language.MavenVersionAdapter;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;

public class ClientInit {

    public ClientInit(IEventBus modBus) {
        CraftTweakerGUI.viewerUtils = ModList.get().isLoaded("emi") ? new EmiViewerUtils() : new JeiViewerUtils();
        CraftTweakerGUI.loaderUtils = new ForgeLoaderUtils();

        MinecraftForge.EVENT_BUS.register(this);
        RecipeTypes.RECIPE_TYPES.register(modBus);
        RecipeSerializers.RECIPE_SERIALIZERS.register(modBus);

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, ForgeConfig.SPEC);
        ModLoadingContext.get().registerExtensionPoint(ConfigScreenHandler.ConfigScreenFactory.class,
                () -> new ConfigScreenHandler.ConfigScreenFactory((minecraft, screen) -> new ChangeListScreen()));

        if (!ModList.get().isLoaded("emi") && !ModList.get().isLoaded("jei")) {
            try {
                Field field = ClientModLoader.class.getDeclaredField("error");
                field.setAccessible(true);
                field.set(null, new ModLoadingException(ModLoadingContext.get().getActiveContainer().getModInfo(),
                        ModLoadingStage.CONSTRUCT, "fml.modloadingissue.missingdependency", null, List.of("jei or emi",
                        CraftTweakerGUI.MOD_ID, MavenVersionAdapter.createFromVersionSpec("[17.3.0.49,)[1.1.6+1.20.4+neoforge,)"), new DefaultArtifactVersion("null"))));
            } catch (Throwable ignored) {}
            throw new IllegalStateException("Either Emi or Jei is required for CraftTweaker GUI to work");
        }

        CraftTweakerGUI.initVanillaRecipeTypes();
        if (!CraftTweakerGUI.isJeiActive()) {
            RecipeTypeManager.addType(new BrewingRecipeType());
        }
        if (ModList.get().isLoaded("mekanism")) {
            RecipeTypeManager.addType(new CrushingRecipeType());
            RecipeTypeManager.addType(new EnrichingRecipeType());
            RecipeTypeManager.addType(new EnergizedSmeltingRecipeType());
            RecipeTypeManager.addType(new SawingRecipeType());
            RecipeTypeManager.addType(new CombiningRecipeType());
            RecipeTypeManager.addType(new InfusingRecipeType());
            RecipeTypeManager.addType(new ReactingRecipeType());
            RecipeTypeManager.addType(new SeperatingRecipeType());
            RecipeTypeManager.addType(new InjectingRecipeType());
            RecipeTypeManager.addType(new PurifyingRecipeType());
            RecipeTypeManager.addType(new OsmiumCompressingRecipeType());
            RecipeTypeManager.addType(new ChemicalInfusingRecipeType());
            RecipeTypeManager.addType(new DissolutingRecipeType());
            RecipeTypeManager.addType(new CrystallizingRecipeType());
            RecipeTypeManager.addType(new ChemicalWashingRecipeType());
            RecipeTypeManager.addType(new CentrifugingRecipeType());
            RecipeTypeManager.addType(new NeutronActivatingRecipeType());
            RecipeTypeManager.addType(new OxidizingRecipeType());
            RecipeTypeManager.addType(new GasConvertingRecipeType());
            RecipeTypeManager.addType(new InfuseTypeConvertingRecipeType());
            RecipeTypeManager.addType(new EnergyConvertingRecipeType());
            RecipeTypeManager.addType(new PigmentExtractingRecipeType());
            RecipeTypeManager.addType(new PigmentMixingRecipeType());
            RecipeTypeManager.addType(new PaintingRecipeType());
            RecipeTypeManager.addType(new EvaporatingRecipeType());
            RecipeTypeManager.addType(new NucleosynthesizingRecipeType());
            RecipeTypeManager.addType(new CondensentratingRecipeType());
            RecipeTypeManager.addType(new DecondensentratingRecipeType());
        }
    }

    @SubscribeEvent
    public void onSave(LevelEvent.Save event) {
        //This is a server event, but we are on the physical client
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
