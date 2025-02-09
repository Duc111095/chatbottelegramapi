package com.ducnh.chatbotapi.core.resolver;

import com.ducnh.chatbotapi.core.BotDispatcher;
import com.ducnh.chatbotapi.model.BotCommand;
import com.ducnh.chatbotapi.model.BotCommandParams;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;

@SuppressWarnings("rawtypes")
@Slf4j
@NoArgsConstructor
public class BotApiMethodResolver implements TypeResolver<BotApiMethod> {
    @Override
    public void resolve(BotApiMethod value, BotCommand command, BotCommandParams botCommandParams) {
        BotDispatcher.getInstance().executeSneakyThrows(value);
        log.debug("Executed API method {}", value);
    }

    @Override
    public Class<BotApiMethod> getType() {
        return BotApiMethod.class;
    }
}
