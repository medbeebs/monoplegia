package dev.medbeebs.monoplegia;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;

public final class MonoplegiaClient implements ClientModInitializer {
    public static final MonoplegiaConfig CONFIG = MonoplegiaConfig.load();

    @Override
    public void onInitializeClient() {
        KeyMapping.Category category = KeyMapping.Category.register(Identifier.fromNamespaceAndPath("monoplegia", "controls"));
        KeyMapping toggle = KeyMappingHelper.registerKeyMapping(new KeyMapping(
                "key.monoplegia.toggle", InputConstants.Type.KEYBOARD, InputConstants.KEY_G, category));
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (toggle.consumeClick()) {
                if (client.player != null && client.gui.screen() == null) toggle(client);
            }
        });
        // FAIL stops the client before sending the interaction packet to the server.
        // PASS leaves the vanilla interaction chain unchanged when the rule does not match.
        UseBlockCallback.EVENT.register((player, level, hand, hit) -> {
            if (!level.isClientSide() || player.isSpectator()) return InteractionResult.PASS;
            String itemId = BuiltInRegistries.ITEM.getKey(player.getItemInHand(hand).getItem()).toString();
            return PlacementPolicy.blocks(CONFIG.blockOffhandLights, hand == InteractionHand.OFF_HAND, itemId)
                    ? InteractionResult.FAIL : InteractionResult.PASS;
        });
    }

    public static void toggle(Minecraft client) {
        CONFIG.blockOffhandLights = !CONFIG.blockOffhandLights;
        CONFIG.save();
        notifyStatus(client);
    }

    public static void notifyStatus(Minecraft client) {
        if (client.player == null) return;
        Component state = Component.translatable(CONFIG.blockOffhandLights
                ? "message.monoplegia.blocked" : "message.monoplegia.allowed")
                .withStyle(CONFIG.blockOffhandLights ? ChatFormatting.RED : ChatFormatting.GREEN);
        Component text = Component.translatable("message.monoplegia.placement", state);
        if (CONFIG.notification == MonoplegiaConfig.Notification.ACTION_BAR) {
            client.player.sendOverlayMessage(text);
        } else {
            client.player.sendSystemMessage(text);
        }
    }
}
