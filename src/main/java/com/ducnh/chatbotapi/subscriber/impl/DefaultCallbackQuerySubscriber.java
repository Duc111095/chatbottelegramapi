package com.ducnh.chatbotapi.subscriber.impl;

import com.ducnh.chatbotapi.subscriber.CallbackQuerySubscriber;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.objects.Update;

@Slf4j
public class DefaultCallbackQuerySubscriber implements CallbackQuerySubscriber {

    @Override
    public void accept(Update update) {
        log.trace("Callback detected");
    }
}
