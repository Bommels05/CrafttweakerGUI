package de.bommels05.ctgui.screen;

import de.bommels05.ctgui.ChangedRecipeManager;
import de.bommels05.ctgui.Config;
import de.bommels05.ctgui.CraftTweakerGUI;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.*;
import net.minecraft.client.gui.screens.DisconnectedScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;

public class ChangeListScreen extends Screen {

    private Button filterAll;
    private Button filterNew;
    private ChangedRecipesList list;

    public ChangeListScreen() {
        super(Component.translatable("ctgui.list.title"));
    }

    @Override
    protected void init() {
        if (minecraft.level != null) {
            Button export = Button.builder(Component.translatable("ctgui.list.export"), button -> {
                ChangedRecipeManager.export();
                MutableComponent text = Component.translatable("ctgui.export_notice");
                if (!minecraft.isLocalServer()) {
                    text = text.append(Component.translatable("ctgui.list.export_server").withStyle(ChatFormatting.RED));
                }
                minecraft.setScreen(new DisconnectedScreen(this, Component.translatable("ctgui.list.export"), text, CommonComponents.GUI_OK));
            }).bounds(this.width / 2 - 75, this.height - 25, 150, 20).build();
            export.visible = Config.editMode;

            addRenderableWidget(export);
            addRenderableWidget(getEditModeButton());
            addRenderableWidget(getListButton());
            addRenderableWidget(filterAll = getFilterAllButton());
            filterAll.active = false;
            addRenderableWidget(filterNew = getFilterNewButton());
            list = new ChangedRecipesList(minecraft, this.width, this.height - 60, 30, false);
            addRenderableWidget(list);
            addRenderableWidget(new ColoredButton(this.width - 25, this.height - (25), 20, 20, Component.literal("?"), 16762624, button -> {
                ChangedRecipeManager.save();
                minecraft.setScreen(new DisconnectedScreen(this, Component.translatable("ctgui.help.title"), Component.translatable("ctgui.help"), CommonComponents.GUI_BACK));
            }, Config.editMode));
        } else {
            minecraft.setScreen(new DisconnectedScreen(null, Component.translatable("ctgui.list.unavailable"), Component.translatable("ctgui.list.only_ingame"), CommonComponents.GUI_BACK));
        }
    }

    private SpriteIconButton getEditModeButton() {
        String path = "icon/edit_mode_" + Config.editMode;
        SpriteIconButton button = SpriteIconButton.builder(Component.empty(), b -> {
            Config.setEditMode(!Config.editMode);
            minecraft.setScreen(new ChangeListScreen());
        }, true).size(20, 20).sprite(new ResourceLocation(CraftTweakerGUI.MOD_ID, path), 16, 16).build();
        button.setX(5);
        button.setY(this.height - 25);
        button.setTooltip(Tooltip.create(Component.translatable("ctgui.list.edit_mode_" + Config.editMode)));
        return button;
    }

    private Button getListButton() {
        Button button = new ColoredButton(30, this.height - 25, 20, 20, Component.literal("CT"), Config.listButton ? 65280 : 16711680, b -> {
            Config.setListButton(!Config.listButton);
            minecraft.setScreen(new ChangeListScreen());
        });
        button.setTooltip(Tooltip.create(Component.translatable("ctgui.list.list_button_" + Config.listButton)));
        return button;
    }

    private Button getFilterAllButton() {
        return Button.builder(Component.translatable("ctgui.list.filter_all"), b -> {
            b.active = false;
            filterNew.active = true;
            list.refill(false);
        }).bounds(this.width / 2 - 105, 5, 100, 20).build();
    }

    private Button getFilterNewButton() {
        return Button.builder(Component.translatable("ctgui.list.filter_new"), b -> {
            b.active = false;
            filterAll.active = true;
            list.refill(true);
        }).bounds(this.width / 2 + 5, 5, 100, 20).build();
    }

    @Override
    public void renderBackground(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        this.renderDirtBackground(graphics);
    }
}
