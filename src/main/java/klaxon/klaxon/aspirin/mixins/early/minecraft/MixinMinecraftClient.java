package klaxon.klaxon.aspirin.mixins.early.minecraft;

import net.minecraft.client.Minecraft;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import klaxon.klaxon.aspirin.Aspirin;
import klaxon.klaxon.aspirin.Timer;

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
        Aspirin.MIXIN_LOG(Timer.TIMES.get(Timer.INDEX - 2).getLeft() + " took " + (previousDelta / 1_000_000L) + " ms");
        Aspirin.MIXIN_LOG("Phase 2: Initialization started at " + (delta / 1_000_000L) + " ms");
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
        Aspirin.MIXIN_LOG(Timer.TIMES.get(Timer.INDEX - 2).getLeft() + " took " + (previousDelta / 1_000_000L) + " ms");
        Aspirin.MIXIN_LOG("Initialization ended at " + (delta / 1_000_000L) + " ms");
    }
}
