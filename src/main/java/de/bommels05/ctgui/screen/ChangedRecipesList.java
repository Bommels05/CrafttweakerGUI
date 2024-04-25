package de.bommels05.ctgui.screen;

import de.bommels05.ctgui.ChangedRecipeManager;
import de.bommels05.ctgui.ChangedRecipeManager.ChangedRecipe.Type;
import de.bommels05.ctgui.Config;
import de.bommels05.ctgui.SupportedRecipe;
import de.bommels05.ctgui.api.UnsupportedViewerException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;

import java.util.ArrayList;
import java.util.List;

public class ChangedRecipesList extends ObjectSelectionList<ChangedRecipesList.Entry> {

    private final Button delete;
    private final Button edit;
    private boolean onlyNew;

    public ChangedRecipesList(Minecraft mc, int width, int height, int y, boolean onlyNew) {
        super(mc, width, height, y, 36);
        this.onlyNew = onlyNew;

        delete = Button.builder(Component.translatable("ctgui.editing.delete"), button -> {
            ChangedRecipeManager.removeChangedRecipe(getSelected().recipe);
            refill(this.onlyNew);
        }).size(100, 20).build();
        edit = Button.builder(Component.translatable("ctgui.list.edit"), button -> {
            minecraft.setScreen(new RecipeEditScreen<>(getSelected().recipe));
        }).size(100, 20).build();

        refill(onlyNew);
    }

    public void refill(boolean onlyNew) {
        this.onlyNew = onlyNew;
        clearEntries();
        List<ChangedRecipeManager.ChangedRecipe<?>> newRecipes = new ArrayList<>();
        for (ChangedRecipeManager.ChangedRecipe<?> recipe : ChangedRecipeManager.getChangedRecipes()) {
            if (!recipe.wasExported()) {
                newRecipes.add(recipe);
            } else if (!onlyNew) {
                addEntry(new Entry(recipe));
            }
        }
        for (ChangedRecipeManager.ChangedRecipe<?> recipe : newRecipes) {
            addEntryToTop(new Entry(recipe));
        }
    }

    @Override
    protected void renderDecorations(GuiGraphics graphics, int mouseX, int mouseY) {
        if (getSelected() != null) {
            SupportedRecipe<?, ?> recipe;
            try {
                //Cache this because it breaks JEIs Cycling otherwise
                if (getSelected().supportedRecipe == null) {
                    getSelected().supportedRecipe = getSelected().recipe.toSupportedRecipe();
                }
                recipe = getSelected().supportedRecipe;
            } catch (UnsupportedViewerException e) {
                //This can only happen when something is uninstalled or removes support for something
                e.display();
                return;
            }
            int width = Math.max(recipe.getWidth(), 100) + 10;
            int left = this.width - width;
            graphics.setColor(0.25F, 0.25F, 0.25F, 1.0F);
            graphics.blit(Screen.BACKGROUND_LOCATION, left, this.getY(), 0, 0, width, minecraft.screen.height - this.getY(), 32, 32);
            graphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
            int buttonX = left + ((width / 2) - 50);
            if (Config.editMode) {
                delete.setPosition(buttonX, this.getBottom() - 50);
                delete.render(graphics, mouseX, mouseY, minecraft.getPartialTick());
                if (getSelected().recipe.getType() != Type.REMOVED) {
                    edit.setPosition(buttonX, this.getBottom() - 25);
                    edit.render(graphics, mouseX, mouseY, minecraft.getPartialTick());
                }
            }
            recipe.render(left + 5, this.getY() + 5, graphics, mouseX, mouseY, minecraft.screen);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!super.mouseClicked(mouseX, mouseY, button)) {
            return Config.editMode && getSelected() != null && (delete.mouseClicked(mouseX, mouseY, button) || (getSelected().recipe.getType() != Type.REMOVED && edit.mouseClicked(mouseX, mouseY, button)));
        }
        return true;
    }

    public class Entry extends ObjectSelectionList.Entry<Entry> {

        private final ChangedRecipeManager.ChangedRecipe<?> recipe;
        private SupportedRecipe<?, ?> supportedRecipe;
        private final Font font = Minecraft.getInstance().font;

        public Entry(ChangedRecipeManager.ChangedRecipe<?> recipe) {
            this.recipe = recipe;
        }

        @Override
        public Component getNarration() {
            return Component.empty();
        }

        @Override
        public void render(GuiGraphics graphics, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean hovering, float partialTick) {
            graphics.drawString(font, recipe.getTitle(), left + 16 + 3, top + 1, 16777215, false);

            List<FormattedCharSequence> id = font.split(Component.literal(recipe.getId()), width - 32 - 2);
            if (id.size() > 2) {
                graphics.drawString(font, id.get(0), left + 16 + 3, top + 12, -8355712, false);
                graphics.drawString(font, "...", left + 16 + 3, top + 12 + font.lineHeight, -8355712, false);
            } else  {
                for(int i = 0; i < id.size(); ++i) {
                    graphics.drawString(font, id.get(i), left + 16 + 3, top + 12 + font.lineHeight * i, -8355712, false);
                }
            }

            graphics.renderItem(recipe.getMainOutput(), left, top);
            graphics.drawCenteredString(font, recipe.getIcon(), left + (16 / 2), top + 17, recipe.getIconColor());
            if (hovering) {
                graphics.fill(left, top, left + width - 4, top + height, -1601138544);
            }
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int p_156427) {
            setSelected(this);
            return true;
        }
    }

}
