package com.skyblockeraddons.client.mixin;

import de.hysky.skyblocker.skyblock.shortcut.ShortcutsConfigListWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(value = ShortcutsConfigListWidget.class, remap = false)
public interface ShortcutsConfigListWidgetInvoker {
    @Invoker("hasDuplicates")
    boolean skyblockeraddons$hasDuplicates();
}