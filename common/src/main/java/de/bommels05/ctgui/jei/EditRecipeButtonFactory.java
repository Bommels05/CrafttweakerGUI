package de.bommels05.ctgui.jei;

import de.bommels05.ctgui.CraftTweakerGUI;
import de.bommels05.ctgui.screen.RecipeEditScreen;
import de.bommels05.ctgui.screen.ScreenUtils;
import mezz.jei.api.gui.IRecipeLayoutDrawable;
import mezz.jei.api.gui.builder.ITooltipBuilder;
import mezz.jei.api.gui.buttons.IButtonState;
import mezz.jei.api.gui.buttons.IIconButtonController;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.gui.inputs.IJeiUserInput;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.advanced.IRecipeButtonControllerFactory;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

class EditRecipeButtonFactory implements IRecipeButtonControllerFactory {
	private final IGuiHelper guiHelper;

	public EditRecipeButtonFactory(IGuiHelper guiHelper) {
		this.guiHelper = guiHelper;
	}

	@Override
	public @Nullable <T> IIconButtonController createButtonController(IRecipeLayoutDrawable<T> recipeLayout) {
		IRecipeCategory<T> recipeCategory = recipeLayout.getRecipeCategory();
		RecipeType<T> recipeType = recipeCategory.getRecipeType();
		T recipe = recipeLayout.getRecipe();
		ResourceLocation recipeId = recipeCategory.getRegistryName(recipe);

		if (!CraftTweakerGUI.shouldShowEditButton(recipeType.getUid(), recipeId, JeiViewerUtils.rightEither(recipeLayout))) {
			return null;
		}
		return new EditButtonController<>(guiHelper, recipeLayout);
	}

	private static class EditButtonController<T> implements IIconButtonController {
		private final IGuiHelper guiHelper;
		private final IRecipeLayoutDrawable<T> recipeLayout;

		public EditButtonController(IGuiHelper guiHelper, IRecipeLayoutDrawable<T> recipeLayout) {
			this.recipeLayout = recipeLayout;
			this.guiHelper = guiHelper;
		}

		@Override
		public void initState(IButtonState state) {
			IDrawableStatic icon = guiHelper.drawableBuilder(ScreenUtils.EDIT_ICON_TEXTURE, 0, 0, 9, 9)
				.setTextureSize(9, 9)
				.addPadding(3, 3, 4, 4)
				.build();
			state.setIcon(icon);
		}

		@Override
		public boolean onPress(IJeiUserInput input) {
			if (input.isSimulate()) {
				return true;
			}

			var supportedRecipe = CraftTweakerGUI.getViewerUtils().toSupportedRecipe(JeiViewerUtils.rightEither(recipeLayout));

			IRecipeCategory<T> recipeCategory = recipeLayout.getRecipeCategory();
			T recipe = recipeLayout.getRecipe();
			ResourceLocation recipeId = recipeCategory.getRegistryName(recipe);

			Minecraft.getInstance().setScreen(new RecipeEditScreen<>(supportedRecipe, recipeId));
			return true;
		}

		@Override
		public void getTooltips(ITooltipBuilder tooltip) {
			tooltip.add(Component.translatable("ctgui.recipe_edit_tooltip"));
		}
	}
}
