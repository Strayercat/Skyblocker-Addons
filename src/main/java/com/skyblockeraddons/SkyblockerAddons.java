package com.skyblockeraddons;

import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SkyblockerAddons implements ModInitializer {
	public static final Logger LOGGER = LoggerFactory.getLogger("skyblocker-addons");

	@Override
	public void onInitialize() {
		LOGGER.info("Skyblocker Addons initialized!");
	}
}
