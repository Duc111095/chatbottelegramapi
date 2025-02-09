package com.ducnh.chatbotapi.subscriber.impl;

import com.ducnh.chatbotapi.subscriber.PosSubscriber;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.objects.Update;

@Slf4j
public class DefaultPosSubscriber implements PosSubscriber {

    @Override
    public void accept(Update update) {
        log.trace("DefaultPosProcessor...");
    }
}
