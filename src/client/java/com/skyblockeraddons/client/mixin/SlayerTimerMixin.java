package com.skyblockeraddons.client.mixin;

import com.skyblockeraddons.client.slayer.SlayerSession;
import de.hysky.skyblocker.skyblock.slayers.features.SlayerTimer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = SlayerTimer.class, remap = false)
public abstract class SlayerTimerMixin {
    @Inject(method = "sendMessage", at = @At("HEAD"))
    private static void skyblockeraddons$recordKill(CallbackInfo ci) {
        SlayerSession.onBossKilled();
    }
}
