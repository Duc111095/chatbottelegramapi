package com.ducnh.chatbotapi.core.resolver;

import com.ducnh.chatbotapi.constant.MessageParseMode;
import com.ducnh.chatbotapi.core.BotDispatcher;
import com.ducnh.chatbotapi.model.BotCommand;
import com.ducnh.chatbotapi.model.BotCommandParams;
import com.ducnh.chatbotapi.utils.TelegramMessageUtils;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.Objects;

@Slf4j
@NoArgsConstructor
public class NumberResolver implements TypeResolver<Number> {

    @Override
    public void resolve(Number value, BotCommand command, BotCommandParams botCommandParams) {
        Message message = botCommandParams.getUpdate().getMessage();
        MessageParseMode parseMode = command.getParseMode();
        TelegramMessageUtils.replyMessage(BotDispatcher.getInstance().getAbsSender(), message, Objects.toString(value), parseMode, command.isDisableWebPagePreview());
        log.debug("Reply Message: {}", value);
    }

    @Override
    public Class<Number> getType() {
        return Number.class;
    }
}
