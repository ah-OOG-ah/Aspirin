package klaxon.klaxon.aspirin.mixins.early.minecraft;

import net.minecraft.client.main.Main;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import klaxon.klaxon.aspirin.Aspirin;
import klaxon.klaxon.aspirin.Timer;

@Mixin(Main.class)
public class MixinMainClient {

    // Find out when MC starts and store that time (| Phase -2?)
    @Inject(method = "main", at = @At(value = "HEAD"), remap = false)
    private static void aspirinOnMain(CallbackInfo ci) {

        Timer.START = Timer.logPhase("Very early MC/Forge setup");
        Aspirin.MIXIN_LOG("Profiling started! Main.main() called! Very early MC/Forge setup started at 0 ms.");
    }
}
