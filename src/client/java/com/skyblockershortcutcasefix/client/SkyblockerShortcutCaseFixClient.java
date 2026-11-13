package com.skyblockershortcutcasefix.client;

import net.fabricmc.api.ClientModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SkyblockerShortcutCaseFixClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
        Logger LOGGER = LoggerFactory.getLogger("skyblocker-shortcut-case-fix");
        LOGGER.info("Skyblocker Shortcut Case Fix Initialized");
	}
}