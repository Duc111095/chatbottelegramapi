package com.ducnh.chatbotapi.subscriber.impl;

import com.ducnh.chatbotapi.subscriber.PreSubscriber;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.objects.Update;

@Slf4j
public class DefaultPreSubscriber implements PreSubscriber {
    @Override
    public void accept(Update update) {
        log.trace("DefaultPreProcessor...");
    }
}
