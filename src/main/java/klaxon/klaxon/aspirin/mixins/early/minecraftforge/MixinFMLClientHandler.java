package klaxon.klaxon.aspirin.mixins.early.minecraftforge;

import static klaxon.klaxon.aspirin.mixinhandler.AspirinCore.MIXIN_LOG;

import cpw.mods.fml.client.FMLClientHandler;
import klaxon.klaxon.aspirin.Timer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@SuppressWarnings({"UnusedMixin", "LoggingSimilarMessage"})
@Mixin(FMLClientHandler.class)
public class MixinFMLClientHandler {

    // Log the time when FMLClientHandler.instance().beginMinecraftLoading gets called (Phase -2 | Phase -1?)
    @Inject(method = "beginMinecraftLoading", at = @At(value = "HEAD"), remap = false)
    public void aspirinOnBeginMCLoading(CallbackInfo ci) {

        // Get the time delta the last step took and the time at which this one started
        long delta = Timer.logPhase("beginMinecraftLoading");
        MIXIN_LOG.info("{} took {} ms", Timer.TIMES.get(Timer.INDEX - 2).getLeft(), delta / 1_000_000L);
        MIXIN_LOG.info("Phase -1 began; beginMinecraftLoading called at {} ms", delta / 1_000_000L);
    }

    // Log when loadMods() is called (Phase -1 | Phase 0)
    @Inject(
            method = "beginMinecraftLoading",
            at = @At(value = "INVOKE", target = "Lcpw/mods/fml/common/Loader;loadMods()V", remap = false),
            remap = false)
    public void aspirinOnLoadMods(CallbackInfo ci) {

        // Get the time delta the last step took and the time at which this one started
        long delta = Timer.logPhase("Constructing mods");
        Timer.SHIFT = -Timer.INDEX;
        long previousDelta = delta - Timer.TIMES.get(Timer.INDEX - 2).getRight();
        MIXIN_LOG.info("{} took {} ms", Timer.TIMES.get(Timer.INDEX - 2).getLeft(), previousDelta / 1_000_000L);
        MIXIN_LOG.info("Phase 0: Constructing Mods began; loadMods called at {} ms", delta / 1_000_000L);
    }

    // Log when preinitializeMods() is called (Phase 0 | Phase 1)
    @Inject(
            method = "beginMinecraftLoading",
            at = @At(value = "INVOKE", target = "Lcpw/mods/fml/common/Loader;preinitializeMods()V", remap = false),
            remap = false)
    public void aspirinOnPreinitializeMods(CallbackInfo ci) {

        // Get the time delta the last step took and the time at which this one started
        long delta = Timer.logPhase("Pre-initialization");
        long previousDelta = delta - Timer.TIMES.get(Timer.INDEX - 2).getRight();
        MIXIN_LOG.info("{} took {} ms", Timer.TIMES.get(Timer.INDEX - 2).getLeft(), previousDelta / 1_000_000L);
        MIXIN_LOG.info("Phase 1: Pre-initialization began; preinitializeMods called at {} ms", delta / 1_000_000L);
    }
}
