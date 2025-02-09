package com.ducnh.chatbotapi.core.resolver;

import com.ducnh.chatbotapi.constant.MediaType;
import com.ducnh.chatbotapi.core.BotDispatcher;
import com.ducnh.chatbotapi.model.BotCommand;
import com.ducnh.chatbotapi.model.BotCommandParams;
import com.ducnh.chatbotapi.utils.FileUtils;
import com.ducnh.chatbotapi.utils.SendMediaUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;

@Slf4j
public class ByteArrayResourceResolver implements TypeResolver<ByteArrayResource> {
    @Override
    public void resolve(ByteArrayResource value, BotCommand command, BotCommandParams botCommandParams) {
        MediaType sendFile = command.getSendFile();
        SendMediaUtils.sendMedia(botCommandParams.getUpdate().getMessage(), FileUtils.getInputFile(value), botCommandParams.getUpdate().getMessage().getChatId(), sendFile, BotDispatcher.getInstance().getAbsSender());
        log.debug("Reply Media: [{}]", sendFile);
    }

    @Override
    public Class<ByteArrayResource> getType() {
        return ByteArrayResource.class;
    }
}
