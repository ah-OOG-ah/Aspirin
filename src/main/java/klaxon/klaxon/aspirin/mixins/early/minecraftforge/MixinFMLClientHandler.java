package klaxon.klaxon.aspirin.mixins.early.minecraftforge;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import cpw.mods.fml.client.FMLClientHandler;
import klaxon.klaxon.aspirin.Aspirin;
import klaxon.klaxon.aspirin.Timer;

@Mixin(FMLClientHandler.class)
public class MixinFMLClientHandler {

    // Log the time when FMLClientHandler.instance().beginMinecraftLoading gets called (Phase -2 | Phase -1?)
    @Inject(method = "beginMinecraftLoading", at = @At(value = "HEAD"), remap = false)
    public void aspirinOnBeginMCLoading(CallbackInfo ci) {

        // Get the time delta the last step took and the time at which this one started
        long delta = Timer.logPhase("beginMinecraftLoading");
        Aspirin.MIXIN_LOG(Timer.TIMES.get(Timer.INDEX - 2).getLeft() + " took " + (delta / 1_000_000L) + " ms");
        Aspirin.MIXIN_LOG("Phase -1 began; beginMinecraftLoading called at " + (delta / 1_000_000L) + " ms");
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
        Aspirin.MIXIN_LOG(Timer.TIMES.get(Timer.INDEX - 2).getLeft() + " took " + (previousDelta / 1_000_000L) + " ms");
        Aspirin.MIXIN_LOG("Phase 0: Constructing Mods began; loadMods called at " + (delta / 1_000_000L) + " ms");
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
        Aspirin.MIXIN_LOG(Timer.TIMES.get(Timer.INDEX - 2).getLeft() + " took " + (previousDelta / 1_000_000L) + " ms");
        Aspirin.MIXIN_LOG(
                "Phase 1: Pre-initialization began; preinitializeMods called at " + (delta / 1_000_000L) + " ms");
    }
}
