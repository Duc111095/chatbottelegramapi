package com.ducnh.chatbotapi.core.resolver;

import com.ducnh.chatbotapi.constant.MediaType;
import com.ducnh.chatbotapi.core.BotDispatcher;
import com.ducnh.chatbotapi.model.BotCommand;
import com.ducnh.chatbotapi.model.BotCommandParams;
import com.ducnh.chatbotapi.utils.SendMediaUtils;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.objects.InputFile;

@Slf4j
public class InputFileResolver implements TypeResolver<InputFile> {
    @Override
    public void resolve(InputFile value, BotCommand command, BotCommandParams botCommandParams) {
        MediaType sendFile = command.getSendFile();
        SendMediaUtils.sendMedia(botCommandParams.getUpdate().getMessage(), value, botCommandParams.getUpdate().getMessage().getChatId(), sendFile, BotDispatcher.getInstance().getAbsSender());
        log.debug("Reply Media: [{}]", sendFile);
    }

    @Override
    public Class<InputFile> getType() {
        return InputFile.class;
    }
}
