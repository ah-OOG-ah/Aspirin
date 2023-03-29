package klaxon.klaxon.aspirin.mixins.early.minecraftforge;

import java.util.List;

import klaxon.klaxon.aspirin.Aspirin;
import klaxon.klaxon.aspirin.Timer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IReloadableResourceManager;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import cpw.mods.fml.client.FMLClientHandler;

@Mixin(FMLClientHandler.class)
public class MixinBeginMCLoading {

    // Log the time of FMLClientHandler.instance().beginMinecraftLoading
    @Inject(method = "beginMinecraftLoading", at = @At(value = "HEAD"), remap = false)
    public void aspirinOnBeginMCLoading(Minecraft mc, List rpl, IReloadableResourceManager rm, CallbackInfo ci) {

        long time = System.nanoTime();
        long delta = time - Timer.START;
        Timer.TIMES.put("on beginMinecraftLoading", delta);
        Aspirin.LOG.info("beginMinecraftLoading called at {} ms", delta / 1_000_000L);
    }
}
