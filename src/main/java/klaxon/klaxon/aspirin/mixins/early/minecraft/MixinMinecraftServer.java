package klaxon.klaxon.aspirin.mixins.early.minecraft;

import klaxon.klaxon.aspirin.mixinhandler.AspirinCore;
import net.minecraft.server.MinecraftServer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import klaxon.klaxon.aspirin.Timer;

@SuppressWarnings("UnusedMixin")
@Mixin(MinecraftServer.class)
public class MixinMinecraftServer {

    // Find out when MC starts and store that time (| Phase -2?)
    @Inject(method = "main", at = @At(value = "HEAD"), remap = false)
    private static void aspirinOnMain(CallbackInfo ci) {

        Timer.START = Timer.logPhase("Very early MC/Forge setup");
        AspirinCore.MIXIN_LOG.info("Profiling started! Main.main() called! Very early MC/Forge setup started at 0 ms.");
    }
}
