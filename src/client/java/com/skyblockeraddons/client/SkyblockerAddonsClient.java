package com.skyblockeraddons.client;

import com.skyblockeraddons.client.slayer.SlayerSession;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.ClientCommands;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SkyblockerAddonsClient implements ClientModInitializer {
    public static final String MOD_ID = "skyblocker-addons";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitializeClient() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, _) -> dispatcher.register(
                ClientCommands.literal("skyblockeraddons")
                        .then(ClientCommands.literal("slayer")
                                .then(ClientCommands.literal("reset").executes(context -> {
                                    SlayerSession.reset();
                                    context.getSource().sendFeedback(Component.literal("Slayer session stats reset").withStyle(ChatFormatting.GREEN));
                                    return 1;
                                })))
        ));
        LOGGER.info("Skyblocker Addons initialized");
    }
}
