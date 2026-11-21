package com.skyblockeraddons.client.slayer;

import de.hysky.skyblocker.skyblock.slayers.SlayerManager;
import de.hysky.skyblocker.skyblock.slayers.SlayerTier;
import de.hysky.skyblocker.skyblock.slayers.SlayerType;
import org.jspecify.annotations.Nullable;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public final class SlayerSession {
    private static final long UNTIMED_DEBOUNCE_MS = 5_000;

    public static final class Stats {
        public int kills;
        public int timedKills;
        public long totalMs;
        public long lastMs = -1;

        public long averageMs() {
            return timedKills == 0 ? -1 : totalMs / timedKills;
        }
    }

    private static final Map<String, Stats> stats = new HashMap<>();
    private static @Nullable Instant lastSpawn;
    private static long lastUntimedAt;

    private SlayerSession() {
    }

    private static String key(SlayerType type, SlayerTier tier) {
        return type.name() + ":" + tier.name();
    }

    public static void onBossKilled() {
        SlayerManager.SlayerQuest quest = SlayerManager.getSlayerQuest();
        if (quest == null) return;

        Instant spawn = quest.bossSpawnTime;
        Stats s;
        if (spawn != null) {
            if (spawn.equals(lastSpawn)) return;
            lastSpawn = spawn;
            Instant death = quest.bossDeathTime != null ? quest.bossDeathTime : Instant.now();
            long duration = Duration.between(spawn, death).toMillis();
            s = stats.computeIfAbsent(key(quest.slayerType, quest.slayerTier), _ -> new Stats());
            s.lastMs = duration;
            s.totalMs += duration;
            s.timedKills++;
        } else {
            long now = System.currentTimeMillis();
            if (now - lastUntimedAt < UNTIMED_DEBOUNCE_MS) return;
            lastUntimedAt = now;
            s = stats.computeIfAbsent(key(quest.slayerType, quest.slayerTier), _ -> new Stats());
        }
        s.kills++;
    }

    public static @Nullable Stats get(SlayerType type, SlayerTier tier) {
        return stats.get(key(type, tier));
    }

    public static long currentFightMs(SlayerManager.SlayerQuest quest) {
        if (quest.bossSpawnTime == null || quest.bossDeathTime != null || quest.bossSpawnTime.equals(lastSpawn)) return -1;
        return Duration.between(quest.bossSpawnTime, Instant.now()).toMillis();
    }

    public static void reset() {
        stats.clear();
    }

    public static String format(long ms) {
        if (ms < 0) return "-";
        if (ms < 60_000) return String.format(Locale.ROOT, "%.2fs", ms / 1000.0);
        return String.format(Locale.ROOT, "%d:%05.2f", ms / 60_000, (ms % 60_000) / 1000.0);
    }
}
