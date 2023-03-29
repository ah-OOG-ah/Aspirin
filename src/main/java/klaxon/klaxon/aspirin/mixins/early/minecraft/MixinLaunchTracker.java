package klaxon.klaxon.aspirin.mixins.early.minecraft;

import klaxon.klaxon.aspirin.Aspirin;
import klaxon.klaxon.aspirin.Timer;

import net.minecraft.client.main.Main;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Main.class)
public class MixinLaunchTracker {

    // Find out when MC starts and store that time
    @Inject(method = "main", at = @At(value = "HEAD"), remap = false)
    private static void aspirinOnMain(String[] p_main_0_, CallbackInfo ci) {

        Timer.START = System.nanoTime();
        Aspirin.LOG.info("Profiling started! Main.main() called!");
    }
}
