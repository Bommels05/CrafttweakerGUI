package de.bommels05.ctgui.jei;

import de.bommels05.ctgui.CraftTweakerGUI;
import de.bommels05.ctgui.screen.RecipeEditScreen;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.gui.handlers.IGuiProperties;
import mezz.jei.api.gui.handlers.IScreenHandler;
import mezz.jei.api.registration.IAdvancedRegistration;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

@JeiPlugin
public class CTGUIJeiPlugin implements IModPlugin {
    public static IJeiRuntime RUNTIME;

    @Override
    public void registerAdvanced(IAdvancedRegistration registration) {
        if (CraftTweakerGUI.isJeiActive()) {
            registration.getJeiHelpers().getAllRecipeTypes().forEach(recipeType -> registration.addRecipeCategoryDecorator(recipeType, new JeiRecipeDecorator<>()));
            registration.addRecipeManagerPlugin(new InjectionRecipeManagerPlugin());
        }
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        if (CraftTweakerGUI.isJeiActive()) {
            registration.addGuiScreenHandler(RecipeEditScreen.class, guiScreen -> {
                if (guiScreen.getRecipe() != null && guiScreen.getRecipe().getRecipe() != null) {
                    return new EditScreenGuiProperties(guiScreen);
                }
                return null;
            });
            registration.addGhostIngredientHandler(RecipeEditScreen.class, new JeiDragAndDropHandler());
        }
    }

    @Override
    public void onRuntimeAvailable(IJeiRuntime runtime) {
        RUNTIME = runtime;
    }

    @Override
    public ResourceLocation getPluginUid() {
        return new ResourceLocation(CraftTweakerGUI.MOD_ID, "jei_plugin");
    }
}
