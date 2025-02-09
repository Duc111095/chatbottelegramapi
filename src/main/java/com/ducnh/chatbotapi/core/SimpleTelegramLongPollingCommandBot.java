package com.ducnh.chatbotapi.core;

import lombok.SneakyThrows;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public class SimpleTelegramLongPollingCommandBot extends TelegramLongPollingBot {
    private final BotProperties botProperties;

    public SimpleTelegramLongPollingCommandBot(BotProperties botProperties) {
        super(botProperties.getToken());
        this.botProperties = botProperties;
    }

    @Override
    public String getBotUsername() {
        return botProperties.getUsername();
    }

    @SneakyThrows
    @Override
    public void onRegister() {
        super.onRegister();
        BotDispatcher.getInstance().onRegisterBot();
    }

    @Override
    public void onUpdateReceived(Update update) {
        BotDispatcher.getInstance().getUpdateSubscriber().consume(Mono.just(update));
    }

    @Override
    public void onUpdatesReceived(List<Update> updates) {
        BotDispatcher.getInstance().getUpdateSubscriber().consume(Flux.fromIterable(updates));
    }
}
