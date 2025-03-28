package klaxon.klaxon.aspirin;

import static klaxon.klaxon.aspirin.Aspirin.MODID;
import static klaxon.klaxon.aspirin.Aspirin.MODNAME;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = MODID, version = Tags.VERSION, name = MODNAME, acceptedMinecraftVersions = "[1.7.10]")
public class Aspirin {

    public static final String MODID = "aspirin";
    public static final String MODNAME = "Aspirin";

    @SidedProxy(clientSide = "klaxon.klaxon.aspirin.ClientProxy", serverSide = "klaxon.klaxon.aspirin.CommonProxy")
    public static CommonProxy proxy;

    @Mod.EventHandler
    @SuppressWarnings("unused")
    public void preInit(FMLPreInitializationEvent ignored) {
        proxy.preInit(ignored);
    }
}
