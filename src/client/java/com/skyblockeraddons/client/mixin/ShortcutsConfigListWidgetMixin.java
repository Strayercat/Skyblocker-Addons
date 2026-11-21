package com.skyblockeraddons.client.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import de.hysky.skyblocker.skyblock.shortcut.ShortcutsConfigListWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Locale;

@Mixin(value = ShortcutsConfigListWidget.class, remap = false)
public abstract class ShortcutsConfigListWidgetMixin {
    @ModifyExpressionValue(
            method = "hasDuplicates",
            at = @At(value = "INVOKE", target = "Lde/hysky/skyblocker/skyblock/shortcut/ShortcutsConfigListWidget$ShortcutEntry;key()Ljava/lang/String;")
    )
    private String skyblockeraddons$caseInsensitiveKey(String key) {
        return key.toLowerCase(Locale.ROOT);
    }
}