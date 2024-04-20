package de.bommels05.ctgui.screen;

import com.mojang.blaze3d.platform.InputConstants;
import de.bommels05.ctgui.ChangedRecipeManager;
import de.bommels05.ctgui.CraftTweakerGUI;
import de.bommels05.ctgui.SupportedRecipe;
import de.bommels05.ctgui.api.AmountedIngredient;
import de.bommels05.ctgui.api.SupportedRecipeType;
import de.bommels05.ctgui.api.UnsupportedRecipeException;
import de.bommels05.ctgui.api.UnsupportedViewerException;
import de.bommels05.ctgui.api.option.RecipeIdFieldRecipeOption;
import de.bommels05.ctgui.api.option.RecipeOption;
import dev.emi.emi.EmiPort;
import dev.emi.emi.EmiRenderHelper;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.SlotWidget;
import dev.emi.emi.runtime.EmiDrawContext;
import dev.emi.emi.screen.EmiScreenManager;
import net.minecraft.ResourceLocationException;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.*;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.screens.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiFunction;

public class RecipeEditScreen<R extends Recipe<?>> extends Screen {

    private final static ResourceLocation HOT_BAR = new ResourceLocation(CraftTweakerGUI.MOD_ID, "textures/gui/hotbar.png");
    private final SupportedRecipe<R, ? extends SupportedRecipeType<R>> recipe;
    private AmountedIngredient dragged = null;
    private List<SlotWidget> hotBarSlots = new ArrayList<>();
    private SlotWidget tagSlot;
    private Ingredient tag;
    public int imageWidth;
    public int imageHeight;
    private int optionsHeight;
    private final Action action;
    private String recipeId;
    private ResourceLocation originalRecipeId;
    private final ChangedRecipeManager.ChangedRecipe<R> change;
    private boolean recipeIdChanged = false;
    private Button saveNew;
    private Button save;
    private EditBox idBox;

    public RecipeEditScreen(SupportedRecipe<R, ? extends SupportedRecipeType<R>> recipe, ResourceLocation recipeId) {
        super(Component.translatable("ctgui.editing.title", ""));
        this.change = null;
        this.recipe = recipe;
        if (recipeId == null) {
            action = Action.NEW;
            this.recipeId = CraftTweakerGUI.MOD_ID + "/new/" + recipe.getType().getId().getPath() + "/";
        } else {
            action = Action.EDIT;
            recipeIdChanged = true;
            this.originalRecipeId = recipeId;
            this.recipeId = getAutoRecipeId(CraftTweakerGUI.MOD_ID + "/new/" + recipeId.getNamespace() + "/" + recipeId.getPath());
        }
    }

    public RecipeEditScreen(ChangedRecipeManager.ChangedRecipe<R> change) {
        super(Component.translatable("ctgui.editing.title", ""));
        this.change = change;
        this.recipe = change.toSupportedRecipe();
        try {
            this.recipe.setRecipe(change.getRecipe());
        } catch (UnsupportedViewerException ignored) {
            //This would be thrown again in init and caught there
        }
        action = Action.EDIT_CHANGE;
        recipeIdChanged = true;
        this.recipeId = change.getId();
    }

    @Override
    protected void init() {
        try {
            changeRecipe(recipe.getType().onInitialize(recipe.getRecipe()));
        } catch (UnsupportedRecipeException e) {
            e.display();
            return;
        }

        if (getOptionsMaxX() >= getMinX()) {
            minecraft.setScreen(new ConfirmScreen(b -> minecraft.setScreen(b ? new OptionsScreen(null, minecraft.options) : null), Component.translatable("ctgui.editing.to_small"), Component.translatable("ctgui.editing.to_small_description"), CommonComponents.GUI_OK, CommonComponents.GUI_CANCEL));
            return;
        }

        this.imageWidth = getMaxX() - getMinX();
        this.imageHeight = getMaxY() - getMinY();

        Component title = Component.translatable("ctgui.editing.title", recipe.getCategoryName());
        addRenderableWidget(new StringWidget((this.width / 2) - (font.width(title) / 2), getMinY(), font.width(title), font.lineHeight, title, this.font));

        saveNew = new Button.Builder(Component.translatable("ctgui.editing.save_new"), button -> {
            //Screen is set to null first to not trigger anti tag collapsing and last to the change list screen so the new change is listed
            minecraft.setScreen(null);
            ChangedRecipeManager.addChangedRecipe(ChangedRecipeManager.ChangedRecipe.added(recipeId, recipe.getRecipe(), recipe.getType()));
            minecraft.setScreen(new ChangeListScreen());
        }).bounds(getMinX() + 5, getMaxY() - 25, 100, 20).
                tooltip(Tooltip.create(Component.translatable(action.isAnyEdit() ? "ctgui.editing.save_new_description_editing" : "ctgui.editing.save_new_description"))).build();
        saveNew.active = true;
        addRenderableWidget(saveNew);

        save = new Button.Builder(Component.translatable("ctgui.editing.save"), button -> {
            minecraft.setScreen(null);
            if (action.isEdit()) {
                ChangedRecipeManager.addChangedRecipe(ChangedRecipeManager.ChangedRecipe.changed(recipeId, originalRecipeId, recipe.getRecipe(), recipe.getType()));
            } else {
                ChangedRecipeManager.removeChangedRecipe(change);
                ChangedRecipeManager.addChangedRecipe(change.withRecipe(recipe.getRecipe()));
            }
            minecraft.setScreen(new ChangeListScreen());
        }).bounds((this.width / 2) - 50, getMaxY() - 25, 100, 20).
                tooltip(Tooltip.create(Component.translatable(action.isEditChange() ? "ctgui.editing.save_description_editing_change" : action.isEdit() ? "ctgui.editing.save_description" : "ctgui.editing.unavailable"))).build();
        save.active = action.isAnyEdit();
        addRenderableWidget(save);

        Button delete = new Button.Builder(Component.translatable("ctgui.editing.delete"), button -> {
            minecraft.setScreen(null);
            ChangedRecipeManager.addChangedRecipe(ChangedRecipeManager.ChangedRecipe.removed(originalRecipeId, recipe.getRecipe(), recipe.getType()));
            minecraft.setScreen(new ChangeListScreen());
        }).bounds(getMaxX() - 105, getMaxY() - 25, 100, 20).
                tooltip(Tooltip.create(Component.translatable(action.isEdit() ? "ctgui.editing.delete_description" : "ctgui.editing.unavailable"))).build();
        delete.active = action.isEdit();
        addRenderableWidget(delete);

        idBox = new EditBox(this.font, (this.width / 2) - (imageWidth - 10) / 2, getMinY() + 15, imageWidth - 10, 18, Component.empty());
        Optional<RecipeOption<?, R>> idOption = recipe.getType().getOptions().stream().filter(option -> option instanceof RecipeIdFieldRecipeOption).findFirst();
        if (idOption.isPresent()) {
            RecipeIdFieldRecipeOption<R> option = (RecipeIdFieldRecipeOption<R>) idOption.get();
            option.addToScreen(this, 0, 0);
            option.supplyEditBox(idBox, (id) -> recipeId = id);
            recipeIdChanged = true;
        } else {
            idBox.setTooltip(Tooltip.create(Component.translatable(action.isEdit() ?  "ctgui.editing.recipe_id_editing" : "ctgui.editing.recipe_id")));
            idBox.setMaxLength(256);
            idBox.setValue(recipeId);
            idBox.setFilter(input -> {
                return input.startsWith(CraftTweakerGUI.MOD_ID + "/new/") && ResourceLocation.isValidPath(input);
            });
            idBox.setResponder(input -> {
                recipeId = input;
                recipeIdChanged = true;
                validate(null);
            });
        }
        addRenderableWidget(idBox);

        Component tagTitle = Component.translatable("ctgui.editing.tag_title");
        addRenderableWidget(new StringWidget((getTagMaxX() / 2) - (font.width(tagTitle) / 2), getTagMinY() + 1, font.width(tagTitle), font.lineHeight, tagTitle, this.font));
        EditBox tagBox = new EditBox(this.font, getTagMinX() + 1, getTagMinY() + 11, 100, 18, tagTitle);
        tagBox.setResponder(input -> {
            EmiIngredient ingredient;
            try {
                TagKey<Item> key = TagKey.create(Registries.ITEM, new ResourceLocation(input));
                tag = Ingredient.of(key);
                //Make the EmiIngredient from the ingredient to display empty tags
                ingredient = EmiIngredient.of(tag);
            } catch (ResourceLocationException e) {
                ingredient = EmiStack.EMPTY;
                tag = Ingredient.EMPTY;
            }
            tagSlot = new SlotWidget(ingredient, 102, getTagMinY() + 11);
        });
        tagBox.setValue("forge:ingots/iron");
        addRenderableWidget(tagBox);

        if (!recipe.getType().getOptions().isEmpty() && !(recipe.getType().getOptions().size() == 1 && idOption.isPresent())) {
            int y = getOptionsMinY() + 2;
            Component optionsTitle = Component.translatable("ctgui.editing.options_title");
            addRenderableWidget(new StringWidget(1, y, font.width(optionsTitle), font.lineHeight, optionsTitle, this.font));
            y += font.lineHeight + 2;
            for (RecipeOption<?, R> option : recipe.getType().getOptions()) {
                if (!(option instanceof RecipeIdFieldRecipeOption)) {
                    option.addToScreen(this, getOptionsMinX() + 1, y);
                    y += option.getHeight() + 2;
                }
            }
            optionsHeight = y - getOptionsMinY();
        } else {
            optionsHeight = -1;
        }

        for (int i = 0; i < 9; i++) {
            hotBarSlots.add(new SlotWidget(EmiStack.of(minecraft.player.getInventory().getItem(i)), getHotBarX() + 7 + i * 18, getMaxY() - 1));
        }

        validate(null);
        EmiScreenManager.addWidgets(this);
    }

    @Override
    public void renderBackground(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.renderBackground(graphics, mouseX, mouseY, partialTick);

        ScreenUtils.renderContainerBackground(graphics, getMinX(), getMinY() - 4, getMaxX() - getMinX(), getMaxY() - (getMinY() - 4));
        ScreenUtils.renderContainerBackground(graphics, getTagMinX() - 5, getTagMinY() - 3, getTagMaxX() - (getTagMinX() - 8), getTagMaxY() - (getTagMinY() - 5));
        if (optionsHeight != -1) {
            ScreenUtils.renderContainerBackground(graphics, getOptionsMinX() - 5, getOptionsMinY() - 2, getOptionsMaxX() + 3 - getOptionsMinX(), getOptionsMaxY() - (getOptionsMinY() - 4));
        }
        graphics.blit(HOT_BAR, getHotBarX(), getMaxY() - 4, 0, 0, 0, 176, 28, 256, 32);

        EmiDrawContext context = EmiDrawContext.wrap(graphics);
        EmiScreenManager.drawBackground(context, mouseX, mouseY, partialTick);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);

        EmiDrawContext context = EmiDrawContext.wrap(graphics);
        context.push();
        EmiPort.setPositionTexShader();
        EmiScreenManager.render(context, mouseX, mouseY, partialTick);
        context.pop();

        recipe.render(getRecipeX(), getRecipeY(), graphics, mouseX, mouseY, this);
        hotBarSlots.forEach(slot -> slot.render(graphics, mouseX, mouseY, partialTick));
        tagSlot.render(graphics, mouseX, mouseY, partialTick);
        if (tagSlot.getBounds().contains(mouseX, mouseY)) {
            EmiRenderHelper.drawTooltip(this, EmiDrawContext.wrap(graphics), tagSlot.getTooltip(mouseX, mouseY), mouseX, mouseY);
        }
        for (SlotWidget slot : hotBarSlots) {
            if (slot.getBounds().contains(mouseX, mouseY)) {
                EmiRenderHelper.drawTooltip(this, EmiDrawContext.wrap(graphics), slot.getTooltip(mouseX, mouseY), mouseX, mouseY);
            }
        }
        if (dragged != null && !dragged.isEmpty()) {
            int x = mouseX - 8;
            int y = mouseY - 8;
            graphics.pose().pushPose();
            graphics.pose().translate(0.0F, 0.0F, 232.0F);
            ItemStack stack = dragged.asStack();
            graphics.renderItem(stack, x, y);
            Font font = IClientItemExtensions.of(stack).getFont(stack, IClientItemExtensions.FontContext.ITEM_COUNT);
            graphics.renderItemDecorations(font == null ? this.font : font, stack, x, y, dragged.isTag() ? "Tag" + (stack.getCount() == 1 ? "" : " " + stack.getCount()) : null);
            graphics.pose().popPose();
        }
        EmiScreenManager.drawForeground(context, mouseX, mouseY, partialTick);
    }

    private int getRecipeX() {
        return (this.width / 2) - recipe.getWidth() / 2;
    }

    private int getRecipeY() {
        return getMinY() + 38;
    }

    public void handleDragAndDrop(int x, int y, AmountedIngredient ingredient) {
        R newRecipe = recipe.getType().onDragAndDrop(recipe.getRecipe(), x - getRecipeX(), y - getRecipeY(), ingredient);
        changeRecipe(newRecipe);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        recipe.mouseClicked(getRecipeX(), getRecipeY(), (int) mouseX, (int) mouseY, mouseButton);

        boolean rightClick = InputConstants.Type.MOUSE.getOrCreate(mouseButton).getValue() == InputConstants.MOUSE_BUTTON_RIGHT;
        R newRecipe = recipe.getType().onClick(recipe.getRecipe(), (int) mouseX - getRecipeX(), (int) mouseY - getRecipeY(), rightClick, this);
        if (!rightClick) {
            if (tagSlot.getBounds().contains((int) mouseX, (int) mouseY) && !tag.isEmpty()) {
                setDragged(new AmountedIngredient(tag, 1));
            } else {
                for (SlotWidget slot : hotBarSlots) {
                    if (slot.getBounds().contains((int) mouseX, (int) mouseY) && !slot.getStack().isEmpty()) {
                        setDragged(AmountedIngredient.of(slot.getStack().getEmiStacks().get(0).getItemStack()));
                    }
                }
            }
        }
        if (changeRecipe(newRecipe)) return true;
        if (EmiScreenManager.mouseClicked(mouseX, mouseY, mouseButton)) {
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double p_94687_, double amount) {
        R newRecipe = recipe.getType().onScroll(recipe.getRecipe(), (int) mouseX - getRecipeX(), (int) mouseY - getRecipeY(), amount > 0);
        if (changeRecipe(newRecipe)) return true;
        if (EmiScreenManager.mouseScrolled(mouseX, mouseY, amount)) {
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, p_94687_, amount);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int mouseButton) {
        R newRecipe = recipe.getType().onReleased(recipe.getRecipe(), (int) mouseX - getRecipeX(), (int) mouseY - getRecipeY(), InputConstants.Type.MOUSE.getOrCreate(mouseButton).getValue() == InputConstants.MOUSE_BUTTON_RIGHT, this);
        if (changeRecipe(newRecipe)) return true;
        if (EmiScreenManager.mouseReleased(mouseX, mouseY, mouseButton)) {
            return true;
        }
        return super.mouseReleased(mouseX, mouseY, mouseButton);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int mouseButton, double deltaX, double deltaY) {
        if (EmiScreenManager.mouseDragged(mouseX, mouseY, mouseButton, deltaX, deltaY)) {
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, mouseButton, deltaX, deltaY);
    }

    @Override
    public boolean charTyped(char c, int modifiers) {
        if (EmiScreenManager.search.charTyped(c, modifiers)) {
            return true;
        }
        return super.charTyped(c, modifiers);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (EmiScreenManager.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    private boolean changeRecipe(R newRecipe) {
        if (newRecipe != null) {
            if (idBox != null && !recipeIdChanged && !recipe.getType().getMainOutput(newRecipe).isEmpty()) {
                recipeId = getAutoRecipeId(recipeId + BuiltInRegistries.ITEM.getKey(recipe.getType().getMainOutput(newRecipe).getItem()).getPath());
                idBox.setValue(recipeId);
                recipeIdChanged = true;
            }
            //Runs once before the buttons are created
            if (save != null) {
                validate(newRecipe);
            }
            int oldWidth = -1;
            int oldHeight = -1;
            if (recipe.getRecipe() != null) {
                oldWidth = recipe.getWidth();
                oldHeight = recipe.getHeight();
            }
            try {
                recipe.setRecipe(newRecipe);
                if (oldWidth != -1 && (oldWidth != recipe.getWidth() || oldHeight != recipe.getHeight())) {
                    clearWidgets();
                    hotBarSlots.clear();
                    init();
                }
            } catch (UnsupportedViewerException e) {
                //todo try rendering with other viewers
                e.display();
                return false;
            }
            return true;
        }
        return false;
    }

    public void validate(R recipe) {
        boolean valid = this.recipe.getType().isValid(recipe != null ? recipe : this.recipe.getRecipe());
        boolean validId = valid && !ChangedRecipeManager.idAlreadyUsed(recipeId) && !recipeId.endsWith("/");
        saveNew.active = validId;
        save.active = valid && ((action.isEdit() && validId) || action.isEditChange());
    }

    private String getAutoRecipeId(String id) {
        AtomicInteger i = new AtomicInteger(0);
        while (ChangedRecipeManager.idAlreadyUsed(id + "_" + i.get())) {
            i.incrementAndGet();
        }
        return id + "_" + i;
    }

    @SuppressWarnings("unchecked")
    public <T, RT extends Recipe<?>> void handleRecipeOption(T value, BiFunction<RT, T, RT> handler) {
        changeRecipe((R) handler.apply((RT) recipe.getRecipe(), value));
    }

    //Exposed for Recipe Options
    @Override
    public <T extends GuiEventListener & Renderable & NarratableEntry> T addRenderableWidget(T widget) {
        return super.addRenderableWidget(widget);
    }

    public Font getFont() {
        return font;
    }

    public int getMinX() {
        return ((this.width / 2) - Math.max(recipe.getWidth() / 2, 330 / 2)) - 5;
    }

    public int getMaxX() {
        return 5 + (this.width / 2) + Math.max(recipe.getWidth() / 2, 330 / 2);
    }

    public int getMinY() {
        return Math.max((((this.height / 2) - (recipe.getHeight() / 2)) - 5) - ((100 + (this.height / 2) + recipe.getHeight() / 2) > this.height - 50 ? ((100 + (this.height / 2) + recipe.getHeight() / 2) - (this.height - 50)) : 0), 30);
    }

    public int getMaxY() {
        return Math.min(100 + (this.height / 2) + recipe.getHeight() / 2, this.height - 50);
    }

    public int getTagMinY() {
        return this.height - 36 - 31;
    }

    public int getTagMaxY() {
        return this.height - 36;
    }

    public int getTagMinX() {
        return 0;
    }

    public int getTagMaxX() {
        return 121;
    }

    public int getOptionsMinY() {
        return 100;
    }

    public int getOptionsMaxY() {
        return 100 + optionsHeight;
    }

    public int getOptionsMinX() {
        return 0;
    }

    public int getOptionsMaxX() {
        return 121;
    }

    private int getHotBarX() {
        return getMinX() + ((getMaxX() - getMinX()) / 2) - 176 / 2;
    }

    public AmountedIngredient getDragged() {
        return dragged;
    }

    public void setDragged(AmountedIngredient dragged) {
        this.dragged = dragged;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    public enum Action {
        NEW,
        EDIT,
        EDIT_CHANGE;

        public boolean isNew() {
            return this == NEW;
        }

        public boolean isEdit() {
            return this == EDIT;
        }

        public boolean isEditChange() {
            return this == EDIT_CHANGE;
        }

        public boolean isAnyEdit() {
            return isEdit() || isEditChange();
        }
    }

}
