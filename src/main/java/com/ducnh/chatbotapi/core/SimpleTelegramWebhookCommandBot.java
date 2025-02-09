package com.ducnh.chatbotapi.core;

import lombok.SneakyThrows;
import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;
import reactor.core.publisher.Mono;

public class SimpleTelegramWebhookCommandBot extends TelegramWebhookBot {
    private final BotProperties botProperties;

    public SimpleTelegramWebhookCommandBot(BotProperties botProperties) {
        super(botProperties.getToken());
        this.botProperties = botProperties;
    }

    @Override
    public BotApiMethod<?> onWebhookUpdateReceived(Update update) {
        BotDispatcher.getInstance().getUpdateSubscriber().consume(Mono.just(update));
        return null;
    }

    @SneakyThrows
    @Override
    public void onRegister() {
        super.onRegister();
        BotDispatcher.getInstance().onRegisterBot();
    }

    @Override
    public String getBotPath() {
        return getBotUsername();
    }

    @Override
    public String getBotUsername() {
        return botProperties.getUsername();
    }
}
