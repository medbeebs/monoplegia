package dev.medbeebs.monoplegia;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.MultiLineTextWidget;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public final class MonoplegiaScreen extends Screen {
    private final Screen parent;
    private Button placement;
    private Button placementReset;
    private Button notification;
    private Button notificationReset;
    private MultiLineTextWidget placementDescription;
    private MultiLineTextWidget notificationDescription;

    public MonoplegiaScreen(Screen parent) {
        super(Component.translatable("screen.monoplegia.title"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        int rowWidth = Math.min(520, width - 32);
        int left = (width - rowWidth) / 2;
        int top = Math.max(40, height / 2 - 65);
        int resetX = left + rowWidth - 60;
        int switchX = resetX - 114;
        int titleWidth = font.width(title);
        addRenderableOnly(new StringWidget((width - titleWidth) / 2, 15, titleWidth, 20, title, font));

        addHeading(left, top, "option.monoplegia.placement");
        placement = addRenderableWidget(Button.builder(placementLabel(), button -> {
            MonoplegiaClient.toggle(minecraft);
            refresh();
        }).bounds(switchX, top, 108, 20).build());
        placementReset = addRenderableWidget(Button.builder(Component.translatable("option.monoplegia.reset"), button -> {
            MonoplegiaClient.CONFIG.blockOffhandLights = MonoplegiaConfig.DEFAULT_BLOCK_OFFHAND_LIGHTS;
            saveAndNotify();
        }).bounds(resetX, top, 60, 20).build());
        placementDescription = addDescription(left, top + 25, rowWidth);

        int second = top + 70;
        addHeading(left, second, "option.monoplegia.notifications");
        notification = addRenderableWidget(Button.builder(notificationLabel(), button -> {
            MonoplegiaConfig config = MonoplegiaClient.CONFIG;
            config.notification = config.notification == MonoplegiaConfig.Notification.ACTION_BAR
                    ? MonoplegiaConfig.Notification.CHAT : MonoplegiaConfig.Notification.ACTION_BAR;
            saveAndNotify();
        }).bounds(switchX, second, 108, 20).build());
        notificationReset = addRenderableWidget(Button.builder(Component.translatable("option.monoplegia.reset"), button -> {
            MonoplegiaClient.CONFIG.notification = MonoplegiaConfig.DEFAULT_NOTIFICATION;
            saveAndNotify();
        }).bounds(resetX, second, 60, 20).build());
        notificationDescription = addDescription(left, second + 25, rowWidth);
        addRenderableWidget(Button.builder(Component.translatable("gui.done"), button -> onClose())
                .bounds(width / 2 - 100, height - 28, 200, 20).build());
        refresh();
    }

    private void addHeading(int x, int y, String key) {
        Component heading = Component.translatable(key);
        addRenderableOnly(new StringWidget(x, y, font.width(heading), 20, heading, font));
    }

    private MultiLineTextWidget addDescription(int x, int y, int maxWidth) {
        return addRenderableOnly(new MultiLineTextWidget(x, y, Component.empty(), font)
                .setMaxWidth(maxWidth));
    }

    private void saveAndNotify() {
        MonoplegiaClient.CONFIG.save();
        MonoplegiaClient.notifyStatus(minecraft);
        refresh();
    }

    private void refresh() {
        placement.setMessage(placementLabel());
        notification.setMessage(notificationLabel());
        placementDescription.setMessage(description(MonoplegiaClient.CONFIG.blockOffhandLights
                ? "option.monoplegia.blocked.description" : "option.monoplegia.allowed.description"));
        notificationDescription.setMessage(description(
                MonoplegiaClient.CONFIG.notification == MonoplegiaConfig.Notification.ACTION_BAR
                        ? "option.monoplegia.action_bar.description" : "option.monoplegia.chat.description"));
        placementReset.active = MonoplegiaClient.CONFIG.blockOffhandLights
                != MonoplegiaConfig.DEFAULT_BLOCK_OFFHAND_LIGHTS;
        notificationReset.active = MonoplegiaClient.CONFIG.notification != MonoplegiaConfig.DEFAULT_NOTIFICATION;
    }

    private Component description(String key) {
        return Component.translatable(key).withStyle(net.minecraft.ChatFormatting.GRAY);
    }

    private Component placementLabel() {
        return Component.translatable(MonoplegiaClient.CONFIG.blockOffhandLights
                ? "option.monoplegia.blocked" : "option.monoplegia.allowed");
    }

    private Component notificationLabel() {
        return Component.translatable(MonoplegiaClient.CONFIG.notification == MonoplegiaConfig.Notification.ACTION_BAR
                ? "option.monoplegia.action_bar" : "option.monoplegia.chat");
    }

    @Override
    public void onClose() {
        minecraft.gui.setScreen(parent);
    }
}
