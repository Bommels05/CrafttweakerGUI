package de.bommels05.ctgui.screen;

import de.bommels05.ctgui.Config;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.layouts.Layout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.multiplayer.WarningScreen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

public class MultiplayerWarningScreen extends WarningScreen {
    private final Screen previous;

    public MultiplayerWarningScreen(Screen previous) {
        super(Component.translatable("ctgui.warning"), Component.translatable("ctgui.editing.no_server"), Component.empty());
        this.previous = previous;
    }

    @Override
    protected Layout addFooterButtons() {
        LinearLayout layout = LinearLayout.horizontal().spacing(8);
        layout.addChild(Button.builder(CommonComponents.GUI_PROCEED, (button) -> {
            Config.acknowledgedMultiplayer = true;
            this.minecraft.setScreen(this.previous);
        }).build());
        layout.addChild(Button.builder(CommonComponents.GUI_BACK, button -> this.onClose()).build());
        return layout;
    }
}
