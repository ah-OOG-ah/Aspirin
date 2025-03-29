package klaxon.klaxon.aspirin.mixins.early.minecraft;

import static klaxon.klaxon.aspirin.mixinhandler.AspirinCore.MIXIN_LOG;

import klaxon.klaxon.aspirin.Timer;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@SuppressWarnings({"UnusedMixin", "LoggingSimilarMessage"})
@Mixin(Minecraft.class)
public class MixinMinecraftClient {

    // Log when finishMinecraftLoading() is called (Phase 1 | Phase 2)
    @Inject(
            method = "startGame",
            at = @At(
                    value = "INVOKE",
                    target = "Lcpw/mods/fml/client/FMLClientHandler;finishMinecraftLoading()V",
                    remap = false),
            remap = false)
    public void aspirinOnFinishMinecraftLoading(CallbackInfo ci) {

        // Get the time delta the last step took and the time at which this one started
        long delta = Timer.logPhase("Initialization");
        long previousDelta = delta - Timer.TIMES.get(Timer.INDEX - 2).getRight();
        MIXIN_LOG.info("{} took {} ms", Timer.TIMES.get(Timer.INDEX - 2).getLeft(), previousDelta / 1_000_000L);
        MIXIN_LOG.info("Phase 2: Initialization started at {} ms", delta / 1_000_000L);
    }

    // Log when onInitializationComplete() is called (Phase 2 |)
    @Inject(
            method = "startGame",
            at = @At(
                    value = "INVOKE",
                    target = "Lcpw/mods/fml/client/FMLClientHandler;onInitializationComplete()V",
                    remap = false))
    public void aspirinAfterBeginMCLoading(CallbackInfo ci) {

        // Get the time delta the last step took and the time at which this one started
        long delta = Timer.logPhase("Finished loading");
        long previousDelta = delta - Timer.TIMES.get(Timer.INDEX - 2).getRight();
        MIXIN_LOG.info("{} took {} ms", Timer.TIMES.get(Timer.INDEX - 2).getLeft(), previousDelta / 1_000_000L);
        MIXIN_LOG.info("Initialization ended at {} ms", delta / 1_000_000L);
    }
}
