package com.ducnh.chatbotapi.core.resolver;

import com.ducnh.chatbotapi.constant.CommonConstant;
import com.ducnh.chatbotapi.constant.MessageParseMode;
import com.ducnh.chatbotapi.core.BotDispatcher;
import com.ducnh.chatbotapi.model.BotCommand;
import com.ducnh.chatbotapi.model.BotCommandParams;
import com.ducnh.chatbotapi.utils.TelegramMessageUtils;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.List;

@Slf4j
@NoArgsConstructor
public class StringResolver implements TypeResolver<String> {
    @Override
    public void resolve(String value, BotCommand command, BotCommandParams botCommandParams) {
        if (StringUtils.isBlank(value)) {
            log.warn("Blank string returned");
            return;
        }
        Message message = botCommandParams.getUpdate().getMessage();
        MessageParseMode parseMode = command.getParseMode();
        if (value.length() > CommonConstant.MAX_MESSAGE_CONTENT_LENGTH) {
            List<String> lineWrap = TelegramMessageUtils.lineWrap(value, CommonConstant.MAX_MESSAGE_CONTENT_LENGTH, false);
            String chatId = message.getChatId() +"";
            for (int i = 0; i < lineWrap.size(); i++) {
                Integer messageId = i == 0 ? message.getMessageId() : null;
                TelegramMessageUtils.replyMessage(BotDispatcher.getInstance().getAbsSender(), chatId, messageId, lineWrap.get(i), parseMode, command.isDisableWebPagePreview());
            }
        } else {
            TelegramMessageUtils.replyMessage(BotDispatcher.getInstance().getAbsSender(), message, value, parseMode, command.isDisableWebPagePreview());
        }
        log.debug("Reply Message: {}", message);
    }

    @Override
    public Class<String> getType() {
        return String.class;
    }
}
