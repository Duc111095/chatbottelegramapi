package com.ducnh.chatbotapi.subscriber;

import org.telegram.telegrambots.meta.bots.AbsSender;

import java.util.function.Consumer;

public interface AfterRegisterBotSubscriber extends Consumer<AbsSender> {
}
