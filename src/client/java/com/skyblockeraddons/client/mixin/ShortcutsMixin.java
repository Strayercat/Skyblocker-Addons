package com.skyblockeraddons.client.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import de.hysky.skyblocker.skyblock.shortcut.Shortcuts;
import de.hysky.skyblocker.utils.data.JsonData;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

@Mixin(value = Shortcuts.class, remap = false)
public abstract class ShortcutsMixin {
    @Unique
    private static final Logger skyblockeraddons$LOGGER = LoggerFactory.getLogger("skyblocker-addons");

    @WrapOperation(
            method = "init",
            at = @At(value = "INVOKE", target = "Lde/hysky/skyblocker/utils/data/JsonData;init()Ljava/util/concurrent/CompletableFuture;")
    )
    private static CompletableFuture<Void> skyblockeraddons$dedupeOnLoad(JsonData<?> instance, Operation<CompletableFuture<Void>> original) {
        return original.call(instance).thenRun(() -> {
            try {
                Shortcuts.ShortcutsRecord data = Shortcuts.shortcuts.getData();
                int removed = skyblockeraddons$dedupe(data.commands(), "command") + skyblockeraddons$dedupe(data.commandArgs(), "command argument");
                if (removed > 0) Shortcuts.shortcuts.save();
            } catch (Exception e) {
                skyblockeraddons$LOGGER.error("Failed to remove case-duplicate shortcuts", e);
            }
        });
    }

    @Unique
    private static int skyblockeraddons$dedupe(Map<String, String> map, String type) {
        Map<String, String> kept = new HashMap<>();
        map.keySet().stream().sorted().forEach(key -> {
            String lower = key.toLowerCase(Locale.ROOT);
            if (!kept.containsKey(lower) || key.equals(lower)) kept.put(lower, key);
        });
        Set<String> survivors = new HashSet<>(kept.values());
        List<String> toRemove = map.keySet().stream().filter(k -> !survivors.contains(k)).toList();
        for (String key : toRemove) {
            skyblockeraddons$LOGGER.info("Removed case-duplicate {} shortcut {} -> {} (kept {})", type, key, map.get(key), kept.get(key.toLowerCase(Locale.ROOT)));
            map.remove(key);
        }
        return toRemove.size();
    }

    @ModifyArg(
            method = "registerCommands",
            at = @At(value = "INVOKE", target = "Lnet/fabricmc/fabric/api/client/command/v2/ClientCommands;literal(Ljava/lang/String;)Lcom/mojang/brigadier/builder/LiteralArgumentBuilder;")
    )
    private static String skyblockeraddons$lowercaseRegistration(String name) {
        return name.toLowerCase(Locale.ROOT);
    }

    @ModifyVariable(method = "modifyCommand", at = @At("HEAD"), argsOnly = true, name = "command")
    private static String skyblockeraddons$lowercaseCommandName(String command) {
        int space = command.indexOf(' ');
        if (space == -1) return command.toLowerCase(Locale.ROOT);
        return command.substring(0, space).toLowerCase(Locale.ROOT) + command.substring(space);
    }

    @WrapOperation(
            method = "modifyCommand",
            at = @At(value = "INVOKE", target = "Lit/unimi/dsi/fastutil/objects/Object2ObjectMap;getOrDefault(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;")
    )
    private static Object skyblockeraddons$caseInsensitiveLookup(Object2ObjectMap<Object, Object> map, Object key, Object defaultValue, Operation<Object> original) {
        Object exact = map.get(key);
        if (exact != null) return exact;
        if (key instanceof String s) {
            for (Map.Entry<Object, Object> entry : map.entrySet()) {
                if (entry.getKey() instanceof String k && k.equalsIgnoreCase(s)) return entry.getValue();
            }
        }
        return original.call(map, key, defaultValue);
    }
}