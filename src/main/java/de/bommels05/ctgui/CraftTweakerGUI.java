package de.bommels05.ctgui;

import com.mojang.logging.LogUtils;
import de.bommels05.ctgui.emi.EmiViewerUtils;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.*;
import net.neoforged.fml.common.Mod;
import org.slf4j.Logger;

@Mod(CraftTweakerGUI.MOD_ID)
public class CraftTweakerGUI {
    public static final String MOD_ID = "ctgui";
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final ViewerUtils viewerUtils = ModList.get().isLoaded("emi") ? new EmiViewerUtils() : /*new JeiViewerUtils()*/ null;;

    public CraftTweakerGUI(IEventBus modBus, Dist dist) {
        if (dist.isClient()) {
            new ClientInit(modBus);
        } else {
            LOGGER.info("CraftTweaker GUI detected on dedicated server, not loading");
        }
    }

    public static ViewerUtils getViewerUtils() {
        return viewerUtils;
    }

}
