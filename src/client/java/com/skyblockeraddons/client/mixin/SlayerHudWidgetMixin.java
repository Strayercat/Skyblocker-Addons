package com.skyblockeraddons.client.mixin;

import com.skyblockeraddons.client.slayer.SlayerSession;
import de.hysky.skyblocker.skyblock.slayers.SlayerHudWidget;
import de.hysky.skyblocker.skyblock.slayers.SlayerManager;
import de.hysky.skyblocker.skyblock.tabhud.config.OptionWidgetCollector;
import de.hysky.skyblocker.skyblock.tabhud.util.Ico;
import de.hysky.skyblocker.skyblock.tabhud.widget.ElementBasedWidget;
import de.hysky.skyblocker.skyblock.tabhud.widget.element.ElementCollector;
import de.hysky.skyblocker.utils.JsonValueInput;
import de.hysky.skyblocker.utils.JsonValueOutput;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = SlayerHudWidget.class, remap = false)
public abstract class SlayerHudWidgetMixin extends ElementBasedWidget {
    @Unique
    private static final String SESSION_STATS_KEY = "skyblockeraddons_session_stats";

    @Unique
    private boolean skyblockeraddons$sessionStats = true;

    private SlayerHudWidgetMixin(Component title, @Nullable Integer colorValue, Information information) {
        super(title, colorValue, information);
    }

    @Inject(method = "updateContent", at = @At("TAIL"))
    private void skyblockeraddons$addSessionStats(CallbackInfo ci) {
        if (!skyblockeraddons$sessionStats) return;
        SlayerManager.SlayerQuest quest = SlayerManager.getSlayerQuest();
        if (quest == null) return;

        SlayerSession.Stats stats = SlayerSession.get(quest.slayerType, quest.slayerTier);
        skyblockeraddons$addLines(this,
                stats == null ? 0 : stats.kills,
                stats == null ? -1 : stats.lastMs,
                stats == null ? -1 : stats.averageMs(),
                SlayerSession.currentFightMs(quest));
    }

    @Inject(method = "updateConfigContent", at = @At("TAIL"))
    private void skyblockeraddons$addSessionStatsPreview(ElementCollector collector, CallbackInfo ci) {
        if (skyblockeraddons$sessionStats) skyblockeraddons$addLines(collector, 12, 24_300, 27_100, -1);
    }

    @Override
    public void getOptionWidgets(@NotNull OptionWidgetCollector collector) {
        super.getOptionWidgets(collector);
        collector.yesNoButton(Component.literal("Session Stats"), b -> {
            skyblockeraddons$sessionStats = b;
            updateConfigPreview();
        }, skyblockeraddons$sessionStats, Component.literal("Show kills, last kill time, average kill time and the current fight time for this session (Skyblocker Addons)"));
    }

    @Override
    public void load(@NotNull JsonValueInput input) {
        super.load(input);
        skyblockeraddons$sessionStats = input.readBooleanOr(SESSION_STATS_KEY, true);
    }

    @Override
    public void save(@NotNull JsonValueOutput output) {
        super.save(output);
        output.writeBool(SESSION_STATS_KEY, skyblockeraddons$sessionStats);
    }

    @Unique
    private static void skyblockeraddons$addLines(ElementCollector collector, int kills, long lastMs, long averageMs, long currentMs) {
        collector.addSimpleIcoText(Ico.IRON_SWORD, "Kills: ", ChatFormatting.LIGHT_PURPLE, String.valueOf(kills));
        collector.addSimpleIcoText(Ico.CLOCK, "Last Kill: ", ChatFormatting.AQUA, SlayerSession.format(lastMs));
        collector.addSimpleIcoText(Ico.COMPASS, "Average: ", ChatFormatting.AQUA, SlayerSession.format(averageMs));
        if (currentMs >= 0) collector.addSimpleIcoText(Ico.CLOCK, "Current: ", ChatFormatting.YELLOW, SlayerSession.format(currentMs));
    }
}