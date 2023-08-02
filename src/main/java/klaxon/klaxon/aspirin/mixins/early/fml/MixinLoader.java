package klaxon.klaxon.aspirin.mixins.early.fml;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import cpw.mods.fml.common.Loader;
import klaxon.klaxon.aspirin.Aspirin;
import klaxon.klaxon.aspirin.Timer;

@Mixin(Loader.class)
public class MixinLoader {

    // Log the time when Loader.instance.loadMods() gets called (Phase -2 | Phase 0?)
    @Inject(method = "loadMods", at = @At("HEAD"), remap = false)
    private static void aspirinOnLoadMods(CallbackInfo ci) {

        // Get the time delta the last step took and the time at which this one started
        long delta = Timer.logPhase("Constructing mods");
        Timer.SHIFT = -Timer.INDEX;
        long previousDelta = delta - Timer.TIMES.get(Timer.INDEX - 2).getRight();
        Aspirin.MIXIN_LOG(Timer.TIMES.get(Timer.INDEX - 2).getLeft() + " took " + (previousDelta / 1_000_000L) + " ms");
        Aspirin.MIXIN_LOG("Phase 0: Constructing Mods began; loadMods called at " + (delta / 1_000_000L) + " ms");
    }

    // Log the time when Loader.instance.preinitializeMods() gets called (Phase 0 | Phase 1)
    @Inject(method = "preinitializeMods", at = @At("HEAD"), remap = false)
    private static void aspirinOnPreinit(CallbackInfo ci) {

        // Get the time delta the last step took and the time at which this one started
        long delta = Timer.logPhase("Pre-initialization");
        Timer.SHIFT = -Timer.INDEX;
        long previousDelta = delta - Timer.TIMES.get(Timer.INDEX - 2).getRight();
        Aspirin.MIXIN_LOG(Timer.TIMES.get(Timer.INDEX - 2).getLeft() + " took " + (previousDelta / 1_000_000L) + " ms");
        Aspirin.MIXIN_LOG(
                "Phase 1: Pre-initialization began; preinitializeMods called at " + (delta / 1_000_000L) + " ms");
    }

    // Log the time when Loader.instance.initializeMods() gets called (Phase 1 | Phase 2)
    @Inject(method = "initializeMods", at = @At("HEAD"), remap = false)
    private static void aspirinOnInit(CallbackInfo ci) {

        // Get the time delta the last step took and the time at which this one started
        long delta = Timer.logPhase("Initialization");
        Timer.SHIFT = -Timer.INDEX;
        long previousDelta = delta - Timer.TIMES.get(Timer.INDEX - 2).getRight();
        Aspirin.MIXIN_LOG(Timer.TIMES.get(Timer.INDEX - 2).getLeft() + " took " + (previousDelta / 1_000_000L) + " ms");
        Aspirin.MIXIN_LOG("Phase 2: Initialization began; initializeMods called at " + (delta / 1_000_000L) + " ms");
    }

    // Log the time when Loader.instance.initializeMods() finishes (Phase 2 |)
    @Inject(method = "initializeMods", at = @At("TAIL"), remap = false)
    private static void aspirinAfterInit(CallbackInfo ci) {

        // Get the time delta the last step took and the time at which this one started
        long delta = Timer.logPhase("After init");
        Timer.SHIFT = -Timer.INDEX;
        long previousDelta = delta - Timer.TIMES.get(Timer.INDEX - 2).getRight();
        Aspirin.MIXIN_LOG(Timer.TIMES.get(Timer.INDEX - 2).getLeft() + " took " + (previousDelta / 1_000_000L) + " ms");
        Aspirin.MIXIN_LOG("Initialization completed at " + (delta / 1_000_000L) + " ms");
    }
}
