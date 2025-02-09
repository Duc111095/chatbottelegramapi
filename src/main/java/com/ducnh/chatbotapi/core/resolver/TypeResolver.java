package com.ducnh.chatbotapi.core.resolver;

import com.ducnh.chatbotapi.model.BotCommand;
import com.ducnh.chatbotapi.model.BotCommandParams;

public interface TypeResolver<T>{
    void resolve(T value, BotCommand command, BotCommandParams botCommandParams);
    Class<T> getType();
}
