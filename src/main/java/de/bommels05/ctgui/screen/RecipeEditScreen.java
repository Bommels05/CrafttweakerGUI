package de.bommels05.ctgui.screen;

import com.mojang.blaze3d.platform.InputConstants;
import de.bommels05.ctgui.ChangedRecipeManager;
import de.bommels05.ctgui.CraftTweakerGUI;
import de.bommels05.ctgui.SupportedRecipe;
import de.bommels05.ctgui.ViewerSlot;
import de.bommels05.ctgui.api.*;
import de.bommels05.ctgui.api.option.RecipeIdFieldRecipeOption;
import de.bommels05.ctgui.api.option.RecipeOption;
import de.bommels05.ctgui.compat.mekanism.ChemicalAmountedIngredient;
import mekanism.api.MekanismAPI;
import mekanism.api.chemical.Chemical;
import mekanism.api.chemical.ChemicalStack;
import net.minecraft.ResourceLocationException;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.*;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.screens.*;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiFunction;

public class RecipeEditScreen<R extends Recipe<?>> extends Screen {

    private final static ResourceLocation HOT_BAR = new ResourceLocation(CraftTweakerGUI.MOD_ID, "textures/gui/hotbar.png");
    private SupportedRecipe<R, ? extends SupportedRecipeType<R>> recipe;
    private AmountedIngredient dragged = null;
    private SpecialAmountedIngredient<?, ?> draggedSpecial = null;
    private List<ViewerSlot> hotBarSlots = new ArrayList<>();
    private ViewerSlot tagSlot;
    private TagKey<?> tag;
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
    private UnsupportedViewerException exception;
    private String tagTypeString;
    private String tagString;

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
        try {
            this.recipe = change.toSupportedRecipe();
            this.recipe.setRecipe(change.getRecipe());
        } catch (UnsupportedViewerException e) {
            this.exception = e;
        }
        action = Action.EDIT_CHANGE;
        recipeIdChanged = true;
        this.recipeId = change.getId();
    }

    @Override
    protected void init() {
        if (exception != null) {
            exception.display();
            return;
        }

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

        //Main buttons
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

        //Recipe id box
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
                return input.startsWith(CraftTweakerGUI.MOD_ID + "/new/") && ResourceLocation.isValidPath(input.replaceAll(" ", "_"));
            });
            idBox.setResponder(input -> {
                recipeId = input.replaceAll(" ", "_");
                if (!input.equals(recipeId)) {
                    idBox.setValue(recipeId);
                }
                recipeIdChanged = true;
                validate(null);
            });
        }
        addRenderableWidget(idBox);

        //Tag boxes
        Component tagTitle = Component.translatable("ctgui.editing.tag_title");
        addRenderableWidget(new StringWidget((getTagMaxX() / 2) - (font.width(tagTitle) / 2), getTagMinY() + 1, font.width(tagTitle), font.lineHeight, tagTitle, this.font));
        EditBox tagBox = new EditBox(this.font, getTagMinX() + 1, getTagMinY() + 11, 119, 18, tagTitle);
        tagBox.setResponder(input -> {
            tagString = input;
            setTag(tagTypeString, input);
        });
        tagBox.setValue("forge:ingots/iron");
        tagBox.setMaxLength(256);
        addRenderableWidget(tagBox);
        Component registryTooltip = Component.translatable("ctgui.editing.tag_registry");
        EditBox tagRegistryBox = new EditBox(this.font, getTagMinX() + 1, getTagMinY() + 31, 100, 18, registryTooltip);
        tagRegistryBox.setResponder(input -> {
            tagTypeString = input;
            setTag(input, tagString);
        });
        tagRegistryBox.setTooltip(Tooltip.create(registryTooltip));
        tagRegistryBox.setValue("minecraft:item");
        tagRegistryBox.setMaxLength(256);
        addRenderableWidget(tagRegistryBox);

        //Recipe options
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
            hotBarSlots.add(CraftTweakerGUI.getViewerUtils().newSlot(minecraft.player.getInventory().getItem(i), getHotBarX() + 7 + i * 18, getMaxY() - 1));
        }

        validate(null);
        CraftTweakerGUI.getViewerUtils().init(this);
    }

    private void setTag(String type, String tag) {
        try {
            if (BuiltInRegistries.REGISTRY.containsKey(new ResourceLocation(type))) {
                this.tag = TagKey.create(ResourceKey.createRegistryKey(new ResourceLocation(type)), new ResourceLocation(tag));
            } else {
                this.tag = null;
            }
        } catch (ResourceLocationException | NullPointerException e) {
            this.tag = null;
        }
        tagSlot = CraftTweakerGUI.getViewerUtils().newSlotSpecial(this.tag == null ? new SpecialAmountedIngredient<>(ItemStack.EMPTY) : new SpecialAmountedIngredient<>(this.tag, 1), 102, getTagMinY() + 31);
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

        CraftTweakerGUI.getViewerUtils().renderBackground(graphics, mouseX, mouseY, partialTick);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);

        CraftTweakerGUI.getViewerUtils().renderStart(graphics, mouseX, mouseY, partialTick);

        //Render first so tooltips don't get cut off
        hotBarSlots.forEach(slot -> slot.render(graphics, mouseX, mouseY, partialTick));
        recipe.render(getRecipeX(), getRecipeY(), graphics, mouseX, mouseY, this);
        if (CraftTweakerGUI.isJeiActive()) {
            //Jei does not render slot backgrounds
            graphics.blit(HOT_BAR, 102, getTagMinY() + 11, 7, 3, 18, 18, 256, 32);
        }
        tagSlot.render(graphics, mouseX, mouseY, partialTick);
        tagSlot.renderTooltip(this, graphics, mouseX, mouseY);
        hotBarSlots.forEach(slot -> slot.renderTooltip(this, graphics, mouseX, mouseY));
        if ((dragged != null && !dragged.isEmpty()) || draggedSpecial != null) {
            int x = mouseX - 8;
            int y = mouseY - 8;
            graphics.pose().pushPose();
            graphics.pose().translate(0.0F, 0.0F, 232.0F);
            if (draggedSpecial != null) {
                CraftTweakerGUI.getViewerUtils().renderIngredientSpecial(draggedSpecial, graphics, x, y, partialTick);
            } else {
                ItemStack stack = dragged.asStack();
                graphics.renderItem(stack, x, y);
                Font font = IClientItemExtensions.of(stack).getFont(stack, IClientItemExtensions.FontContext.ITEM_COUNT);
                graphics.renderItemDecorations(font == null ? this.font : font, stack, x, y, dragged.isTag() ? "Tag" + (stack.getCount() == 1 ? "" : " " + stack.getCount()) : null);
            }
            graphics.pose().popPose();
        }
        CraftTweakerGUI.getViewerUtils().renderEnd(graphics, mouseX, mouseY, partialTick);
    }

    public int getRecipeX() {
        return (this.width / 2) - recipe.getWidth() / 2;
    }

    public int getRecipeY() {
        return getMinY() + 38;
    }

    public SupportedRecipeType<R> getRecipeType() {
        return recipe.getType();
    }

    public void handleDragAndDrop(int x, int y, AmountedIngredient ingredient) {
        R newRecipe = recipe.getType().onDragAndDrop(recipe.getRecipe(), x - getRecipeX(), y - getRecipeY(), ingredient);
        changeRecipe(newRecipe);
    }

    public <T> void handleDragAndDropSpecial(int x, int y, T ingredient) {
        R newRecipe = recipe.getType().onDragAndDropSpecial(recipe.getRecipe(), x - getRecipeX(), y - getRecipeY(), getRightImplementation(new SpecialAmountedIngredient<>(ingredient)));
        changeRecipe(newRecipe);
    }

    private SpecialAmountedIngredient<?, ?> getRightImplementation(SpecialAmountedIngredient<?, ?> ingredient) {
        if (ingredient.isStack()) {
            if (ingredient.getStack() instanceof FluidStack stack) {
                return new FluidAmountedIngredient(stack, ingredient.shouldUseAmount() ? ingredient.getAmount() : stack.getAmount());
            } else if (ModList.get().isLoaded("mekanism") && ingredient.getStack() instanceof ChemicalStack<?> stack) {
                return new ChemicalAmountedIngredient<>(stack, ingredient.shouldUseAmount() ? ingredient.getAmount() : (int) stack.getAmount());
            }
        } else {
            TagKey<?> tag = ingredient.getTag();
            if (tag.isFor(Registries.FLUID)) {
                return new FluidAmountedIngredient((TagKey<Fluid>) tag, ingredient.getAmount());
            } else if (ModList.get().isLoaded("mekanism") && (
                    tag.isFor(MekanismAPI.GAS_REGISTRY_NAME) ||
                    tag.isFor(MekanismAPI.INFUSE_TYPE_REGISTRY_NAME) ||
                    tag.isFor(MekanismAPI.SLURRY_REGISTRY_NAME) ||
                    tag.isFor(MekanismAPI.PIGMENT_REGISTRY_NAME))) {
                return new ChemicalAmountedIngredient(tag, ingredient.getAmount());
            }
        }
        return ingredient;
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        recipe.mouseClicked(getRecipeX(), getRecipeY(), (int) mouseX, (int) mouseY, mouseButton);

        boolean rightClick = InputConstants.Type.MOUSE.getOrCreate(mouseButton).getValue() == InputConstants.MOUSE_BUTTON_RIGHT;
        R newRecipe = recipe.getType().onClick(recipe.getRecipe(), (int) mouseX - getRecipeX(), (int) mouseY - getRecipeY(), rightClick, this);
        if (!rightClick) {
            if (tagSlot.mouseOver((int) mouseX, (int) mouseY) && tag != null) {
                if (tag.isFor(Registries.ITEM)) {
                    setDragged(new AmountedIngredient(Ingredient.of((TagKey<Item>) tag), 1));
                } else {
                    setDraggedSpecial(getRightImplementation(new SpecialAmountedIngredient<>(tag, 1)));
                }
            } else {
                for (ViewerSlot slot : hotBarSlots) {
                    if (slot.mouseOver((int) mouseX, (int) mouseY) && !slot.getStack().isEmpty()) {
                        setDragged(AmountedIngredient.of(slot.getStack()));
                    }
                }
            }
        }
        if (changeRecipe(newRecipe)) return true;
        if (CraftTweakerGUI.getViewerUtils().mouseClicked(mouseX, mouseY, mouseButton)) {
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double p_94687_, double amount) {
        R newRecipe = recipe.getType().onScroll(recipe.getRecipe(), (int) mouseX - getRecipeX(), (int) mouseY - getRecipeY(), amount > 0);
        if (changeRecipe(newRecipe)) return true;
        if (CraftTweakerGUI.getViewerUtils().mouseScrolled(mouseX, mouseY, amount)) {
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, p_94687_, amount);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int mouseButton) {
        R newRecipe = recipe.getType().onReleased(recipe.getRecipe(), (int) mouseX - getRecipeX(), (int) mouseY - getRecipeY(), InputConstants.Type.MOUSE.getOrCreate(mouseButton).getValue() == InputConstants.MOUSE_BUTTON_RIGHT, this);
        if (changeRecipe(newRecipe)) return true;
        if (CraftTweakerGUI.getViewerUtils().mouseReleased(mouseX, mouseY, mouseButton)) {
            return true;
        }
        return super.mouseReleased(mouseX, mouseY, mouseButton);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int mouseButton, double deltaX, double deltaY) {
        if (CraftTweakerGUI.getViewerUtils().mouseDragged(mouseX, mouseY, mouseButton, deltaX, deltaY)) {
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, mouseButton, deltaX, deltaY);
    }

    @Override
    public boolean charTyped(char c, int modifiers) {
        if (CraftTweakerGUI.getViewerUtils().charTyped(c, modifiers)) {
            return true;
        }
        return super.charTyped(c, modifiers);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (CraftTweakerGUI.getViewerUtils().keyPressed(keyCode, scanCode, modifiers)) {
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
        return this.height - 36 - 31 - 20;
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

    public SpecialAmountedIngredient<?, ?> getDraggedSpecial() {
        return draggedSpecial;
    }

    public void setDragged(AmountedIngredient dragged) {
        this.dragged = dragged;
    }

    public void setDraggedSpecial(SpecialAmountedIngredient<?, ?> draggedSpecial) {
        this.draggedSpecial = draggedSpecial;
    }

    public ResourceLocation getOriginalRecipeId() {
        return originalRecipeId;
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
