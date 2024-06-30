package de.bommels05.ctgui.api;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.DisconnectedScreen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

public class UnsupportedViewerException extends Exception {

    private final Component message;

    public UnsupportedViewerException(Component message) {
        this.message = message;
    }

    public UnsupportedViewerException() {
        this.message = Component.translatable("ctgui.editing.unsupported_viewer");
    }

    public void display() {
        Minecraft.getInstance().setScreen(new DisconnectedScreen(null, Component.translatable("ctgui.editing.title", Component.translatable("ctgui.unsupported")), message, CommonComponents.GUI_OK));
    }

}
