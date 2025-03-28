package klaxon.klaxon.aspirin;

import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import klaxon.klaxon.aspirin.mixinhandler.AspirinCore;

public class CommonProxy {
    public void preInit(FMLPreInitializationEvent ignored) {
        AspirinCore.LOG.info("Aspirin is a non-steroidal anti-inflammatory agent and a strong blood thinner.");
        AspirinCore.LOG.info("Don't take this with alcohol!");
    }
}
