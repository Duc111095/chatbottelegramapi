package com.ducnh.chatbotapi.subscriber.impl;

import com.ducnh.chatbotapi.subscriber.NonCommandUpdateSubscriber;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.objects.Update;

@Slf4j
public class DefaultNonCommandUpdateSubscriber implements NonCommandUpdateSubscriber {
    @Override
    public void accept(Update update) {
        log.warn("Not a bot's command");
    }
}
