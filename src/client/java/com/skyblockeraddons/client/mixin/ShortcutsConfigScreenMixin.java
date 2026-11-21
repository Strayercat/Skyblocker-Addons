package com.skyblockeraddons.client.mixin;

import de.hysky.skyblocker.skyblock.shortcut.Shortcuts;
import de.hysky.skyblocker.skyblock.shortcut.ShortcutsConfigListWidget;
import de.hysky.skyblocker.skyblock.shortcut.ShortcutsConfigScreen;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ShortcutsConfigScreen.class, remap = false)
public abstract class ShortcutsConfigScreenMixin {
    @Shadow
    private ShortcutsConfigListWidget shortcutsConfigListWidget;

    @Shadow
    protected abstract void checkForDuplicates();

    @Unique
    private Boolean skyblockeraddons$lastDuplicates;

    @Inject(method = "updateButtons", at = @At("TAIL"))
    private void skyblockeraddons$resetDuplicateState(CallbackInfo ci) {
        skyblockeraddons$lastDuplicates = null;
    }

    @Inject(method = "extractRenderState", at = @At("HEAD"))
    private void skyblockeraddons$liveDuplicateCheck(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a, CallbackInfo ci) {
        if (!Shortcuts.isShortcutsLoaded()) return;
        boolean duplicates = ((ShortcutsConfigListWidgetInvoker) shortcutsConfigListWidget).skyblockeraddons$hasDuplicates();
        if (skyblockeraddons$lastDuplicates == null || duplicates != skyblockeraddons$lastDuplicates) {
            skyblockeraddons$lastDuplicates = duplicates;
            checkForDuplicates();
        }
    }
}