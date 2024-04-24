package de.bommels05.ctgui.jei;

import de.bommels05.ctgui.ChangedRecipeManager;
import de.bommels05.ctgui.SupportedRecipe;
import de.bommels05.ctgui.ViewerSlot;
import de.bommels05.ctgui.ViewerUtils;
import de.bommels05.ctgui.api.SupportedRecipeType;
import de.bommels05.ctgui.api.UnsupportedViewerException;
import mezz.jei.api.gui.IRecipeLayoutDrawable;
import mezz.jei.api.recipe.IRecipeManager;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static de.bommels05.ctgui.jei.CTGUIJeiPlugin.RUNTIME;

public class JeiViewerUtils implements ViewerUtils<IRecipeLayoutDrawable<RecipeHolder<? extends Recipe<?>>>> {
    public static JeiViewerUtils INSTANCE;
    protected final List<ChangedRecipeManager.ChangedRecipe<?>> changedRecipes = new ArrayList<>();

    public JeiViewerUtils() {
        if (INSTANCE != null) {
            throw new IllegalStateException("Instance already set");
        }
        INSTANCE = this;
    }

    @Override
    public <T extends Recipe<?>> void inject(ChangedRecipeManager.ChangedRecipe<T> recipe) {
        changedRecipes.add(recipe);
    }

    @Override
    public <T extends Recipe<?>> void unInject(ChangedRecipeManager.ChangedRecipe<T> recipe) {
        changedRecipes.remove(recipe);
    }

    @Override
    public boolean isCustomTagRecipe(IRecipeLayoutDrawable<RecipeHolder<? extends Recipe<?>>> recipe) {
        return false;
    }

    @Override
    public Component getCategoryName(ResourceLocation id) {
        try {
            return getCategory(id).getTitle();
        } catch (UnsupportedViewerException e) {
            return Component.translatable("ctgui.unsupported");
        }
    }

    @Override
    public <R2 extends Recipe<?>, T extends SupportedRecipeType<R2>> SupportedRecipe<R2, T> toSupportedRecipe(IRecipeLayoutDrawable<RecipeHolder<? extends Recipe<?>>> recipe) {
        return new JeiSupportedRecipe<R2, T>((IRecipeLayoutDrawable<RecipeHolder<?>>) recipe);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <R2 extends Recipe<?>> IRecipeLayoutDrawable<RecipeHolder<?>> getViewerRecipe(SupportedRecipeType<R2> type, R2 recipe) throws UnsupportedViewerException {
        IRecipeManager manager = CTGUIJeiPlugin.RUNTIME.getRecipeManager();
        return (IRecipeLayoutDrawable<RecipeHolder<?>>) (IRecipeLayoutDrawable<?>) manager.createRecipeLayoutDrawable(
                manager.createRecipeCategoryLookup().limitTypes(List.of(manager.getRecipeType(type.getId()).orElseThrow(UnsupportedViewerException::new))).get().map(
                        c -> (IRecipeCategory<RecipeHolder<R2>>) c).findFirst().orElseThrow(UnsupportedViewerException::new), new RecipeHolder<>(type.getId(), recipe),
                RUNTIME.getJeiHelpers().getFocusFactory().getEmptyFocusGroup()).orElseThrow(UnsupportedViewerException::new);
    }

    @Override
    public ViewerSlot newSlot(Ingredient ingredient, int x, int y) {
        return new JeiViewerSlot(ingredient, x, y);
    }

    @Override
    public ViewerSlot newSlot(ItemStack stack, int x, int y) {
        return new JeiViewerSlot(stack, x, y);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        return false;
    }

    @Override
    public boolean charTyped(char c, int modifiers) {
        return false;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int mouseButton, double deltaX, double deltaY) {
        return false;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int mouseButton) {
        return false;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        return false;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        return false;
    }

    @Override
    public void renderStart(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {}

    @Override
    public void renderEnd(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {}

    @Override
    public void renderBackground(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {}

    @Override
    public void init(Screen screen) {}

    public static IRecipeCategory<?> getCategory(ResourceLocation id) throws UnsupportedViewerException {
        IRecipeManager manager = CTGUIJeiPlugin.RUNTIME.getRecipeManager();
        return manager.createRecipeCategoryLookup().limitTypes(List.of(manager.getRecipeType(id).orElseThrow(UnsupportedViewerException::new))).get().findFirst().orElseThrow(UnsupportedViewerException::new);
    }
}
