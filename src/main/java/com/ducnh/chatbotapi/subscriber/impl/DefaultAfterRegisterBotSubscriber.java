package com.ducnh.chatbotapi.subscriber.impl;

import com.ducnh.chatbotapi.subscriber.AfterRegisterBotSubscriber;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.bots.AbsSender;

@Slf4j
public class DefaultAfterRegisterBotSubscriber implements AfterRegisterBotSubscriber {
    @Override
    public void accept(AbsSender absSender) {
        log.trace("AfterRegisterBot detected!");
    }
}
