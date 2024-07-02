package de.bommels05.ctgui.screen;

import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.network.chat.Component;

public class ClosingConfirmScreen extends ConfirmScreen {
    public ClosingConfirmScreen(BooleanConsumer consumer, Component title, Component message) {
        super(consumer, title, message);
    }

    public ClosingConfirmScreen(BooleanConsumer booleanConsumer, Component title, Component message, Component yesButton, Component noButton) {
        super(booleanConsumer, title, message, yesButton, noButton);
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return true;
    }

    @Override
    public boolean keyPressed(int i, int j, int k) {
        if (i == 256) {
            //Don't let ESC trigger the consumer
            return false;
        } else {
            return super.keyPressed(i, j, k);
        }
    }
}
