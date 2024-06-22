package de.bommels05.ctgui.jei;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.datafixers.util.Either;
import de.bommels05.ctgui.ChangedRecipeManager;
import de.bommels05.ctgui.SupportedRecipe;
import de.bommels05.ctgui.ViewerSlot;
import de.bommels05.ctgui.ViewerUtils;
import de.bommels05.ctgui.api.SpecialAmountedIngredient;
import de.bommels05.ctgui.api.SupportedRecipeType;
import de.bommels05.ctgui.api.UnsupportedViewerException;
import mezz.jei.api.gui.IRecipeLayoutDrawable;
import mezz.jei.api.ingredients.ITypedIngredient;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static de.bommels05.ctgui.jei.CTGUIJeiPlugin.RUNTIME;

public class JeiViewerUtils implements ViewerUtils<Either<IRecipeLayoutDrawable<RecipeHolder<? extends Recipe<?>>>, IRecipeLayoutDrawable<? extends Recipe<?>>>> {
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
    public boolean isCustomTagRecipe(Either<IRecipeLayoutDrawable<RecipeHolder<? extends Recipe<?>>>, IRecipeLayoutDrawable<? extends Recipe<?>>> recipe) {
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
    @SuppressWarnings("unchecked")
    public <R2 extends Recipe<?>, T extends SupportedRecipeType<R2>> SupportedRecipe<R2, T> toSupportedRecipe(Either<IRecipeLayoutDrawable<RecipeHolder<? extends Recipe<?>>>, IRecipeLayoutDrawable<? extends Recipe<?>>> recipe) {
        return new JeiSupportedRecipe<>((Either<IRecipeLayoutDrawable<RecipeHolder<R2>>, IRecipeLayoutDrawable<R2>>) (Either<?, ?>) recipe);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <R2 extends Recipe<?>> Either<IRecipeLayoutDrawable<RecipeHolder<? extends Recipe<?>>>, IRecipeLayoutDrawable<? extends Recipe<?>>> getViewerRecipe(SupportedRecipeType<R2> type, R2 recipe) throws UnsupportedViewerException {
        Optional<IRecipeLayoutDrawable<RecipeHolder<R2>>> holderRecipe = tryGetViewerRecipe(type, new RecipeHolder<>(type.getId(), recipe));
        if (holderRecipe.isPresent()) {
            return Either.left((IRecipeLayoutDrawable<RecipeHolder<? extends Recipe<?>>>) (IRecipeLayoutDrawable<?>) holderRecipe.get());
        } else {
            return Either.right(tryGetViewerRecipe(type, recipe).orElseThrow(UnsupportedViewerException::new));
        }
    }

    @SuppressWarnings("unchecked")
    private <R2> Optional<IRecipeLayoutDrawable<R2>> tryGetViewerRecipe(SupportedRecipeType<?> type, R2 recipe) throws UnsupportedViewerException {
        IRecipeManager manager = CTGUIJeiPlugin.RUNTIME.getRecipeManager();
        IRecipeCategory<R2> recipeCategory = manager.createRecipeCategoryLookup().limitTypes(List.of(manager.getRecipeType(type.getId()).orElseThrow(
                UnsupportedViewerException::new))).get().map(c -> (IRecipeCategory<R2>) c).findFirst().orElseThrow(
                UnsupportedViewerException::new);
        try {
            recipeCategory.setRecipe(null, recipe, null);
        } catch (ClassCastException e) {
            //Try it first or else it would be logged by JEI
            return Optional.empty();
        } catch (Throwable ignored) {}
        return manager.createRecipeLayoutDrawable(recipeCategory, recipe, RUNTIME.getJeiHelpers().getFocusFactory().getEmptyFocusGroup());
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
    public <S, T> ViewerSlot newSlotSpecial(SpecialAmountedIngredient<S, T> ingredient, int x, int y) {
        return new JeiViewerSlot(ingredient, x, y);
    }

    @Override
    public <S, T> void renderIngredientSpecial(SpecialAmountedIngredient<S, T> ingredient, GuiGraphics graphics, int x, int y, float partialTick) {
        List<S> ingredients = ingredient.getStacks();
        if (!ingredients.isEmpty()) {
            ITypedIngredient<S> typedIngredient = RUNTIME.getIngredientManager().createTypedIngredient(ingredients.get(0)).orElseThrow(() -> new IllegalArgumentException("Unsupported ingredient: " + ingredients.get(0)));
            PoseStack pose = graphics.pose();
            pose.pushPose();
            pose.translate(x, y, 232);
            RUNTIME.getIngredientManager().getIngredientRenderer(typedIngredient.getIngredient()).render(graphics, typedIngredient.getIngredient());
            pose.popPose();
        }
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

    @SuppressWarnings("unchecked")
    public static Either<IRecipeLayoutDrawable<RecipeHolder<? extends Recipe<?>>>, IRecipeLayoutDrawable<? extends Recipe<?>>> rightEither(IRecipeLayoutDrawable<?> recipe) {
        if (recipe.getRecipe() instanceof RecipeHolder) {
            return Either.left((IRecipeLayoutDrawable<RecipeHolder<? extends Recipe<?>>>) recipe);
        } else {
            return Either.right((IRecipeLayoutDrawable<? extends Recipe<?>>) recipe);
        }
    }
}
