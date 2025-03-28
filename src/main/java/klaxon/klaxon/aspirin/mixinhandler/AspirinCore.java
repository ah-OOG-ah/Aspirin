package klaxon.klaxon.aspirin.mixinhandler;

import static klaxon.klaxon.aspirin.Aspirin.MODNAME;

import com.gtnewhorizon.gtnhmixins.IEarlyMixinLoader;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@IFMLLoadingPlugin.MCVersion("1.7.10")
@IFMLLoadingPlugin.SortingIndex(42000) // no idea if this is important
public class AspirinCore implements IFMLLoadingPlugin, IEarlyMixinLoader {

    public static final Logger LOG = LogManager.getLogger(MODNAME);
    public static final Logger MIXIN_LOG = LogManager.getLogger(MODNAME + " Mixins");

    // The mixin config JSON
    @Override
    public String getMixinConfig() {
        return "mixins.aspirin.early.json";
    }

    // Get the mixins
    @Override
    public List<String> getMixins(Set<String> loadedCoreMods) {

        // Holding arrays
        final List<String> mixins = new ArrayList<>();
        final List<String> notLoading = new ArrayList<>();

        for (Mixins mixin : Mixins.values()) {
            if (mixin.phase == Mixins.Phase.EARLY) {
                if (mixin.shouldLoad(loadedCoreMods, Collections.emptySet())) {
                    mixins.addAll(mixin.mixinClasses);
                } else {
                    notLoading.addAll(mixin.mixinClasses);
                }
            }
        }
        LOG.info("Not loading the following EARLY mixins: {}", notLoading.toString());
        return mixins;
    }

    // We don't do ASM here
    @Override
    public String[] getASMTransformerClass() {
        return new String[] {};
    }

    // We don't need to inject a coremod
    @Override
    public String getModContainerClass() {
        return null;
    }

    // No prelaunch setup needed
    @Override
    public String getSetupClass() {
        return null;
    }

    // Still no coremod
    @Override
    public void injectData(Map<String, Object> data) {}

    // Access transformers? What are those?
    @Override
    public String getAccessTransformerClass() {
        return null;
    }
}
