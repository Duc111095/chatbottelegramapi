package com.ducnh.chatbotapi.subscriber.impl;

import com.ducnh.chatbotapi.subscriber.CommandNotFoundUpdateSubscriber;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.objects.Update;

@Slf4j
public class DefaultCommandNotFoundUpdateSubscriber implements CommandNotFoundUpdateSubscriber {
    @Override
    public void accept(Update update, String cmd) {
        log.warn("No route match for command: {}", cmd);
    }
}
