package de.bommels05.ctgui.api;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.DisconnectedScreen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

public class UnsupportedRecipeException extends Exception {

    private final Component message;

    public UnsupportedRecipeException(Component message) {
        this.message = message;
    }

    public UnsupportedRecipeException() {
        this.message = Component.translatable("ctgui.editing.unsupported");
    }

    public void display() {
        Minecraft.getInstance().setScreen(new DisconnectedScreen(null, Component.translatable("ctgui.editing.title", "Unsupported"), message, CommonComponents.GUI_OK));
    }

}
