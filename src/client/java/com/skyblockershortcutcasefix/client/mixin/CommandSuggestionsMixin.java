package com.skyblockershortcutcasefix.client.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.StringReader;
import net.minecraft.client.gui.components.CommandSuggestions;
import net.minecraft.client.multiplayer.ClientSuggestionProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Locale;

@Mixin(CommandSuggestions.class)
public abstract class CommandSuggestionsMixin {
    @WrapOperation(
            method = "updateCommandInfo",
            at = @At(value = "INVOKE", target = "Lcom/mojang/brigadier/CommandDispatcher;parse(Lcom/mojang/brigadier/StringReader;Ljava/lang/Object;)Lcom/mojang/brigadier/ParseResults;")
    )
    private ParseResults<ClientSuggestionProvider> shortcutcasefix$caseInsensitiveParse(CommandDispatcher<ClientSuggestionProvider> dispatcher, StringReader reader, Object source, Operation<ParseResults<ClientSuggestionProvider>> original) {
        ParseResults<ClientSuggestionProvider> result = original.call(dispatcher, reader, source);
        if (!result.getContext().getNodes().isEmpty()) return result;

        String text = reader.getString();
        int start = reader.getCursor();
        int end = text.indexOf(' ', start);
        if (end == -1) end = text.length();

        String word = text.substring(start, end);
        String lower = word.toLowerCase(Locale.ROOT);
        if (word.equals(lower)) return result;

        StringReader lowered = new StringReader(text.substring(0, start) + lower + text.substring(end));
        lowered.setCursor(start);
        ParseResults<ClientSuggestionProvider> alt = original.call(dispatcher, lowered, source);
        if (alt.getContext().getNodes().isEmpty()) return result;

        StringReader restored = new StringReader(text);
        restored.setCursor(alt.getReader().getCursor());
        return new ParseResults<>(alt.getContext(), restored, alt.getExceptions());
    }
}