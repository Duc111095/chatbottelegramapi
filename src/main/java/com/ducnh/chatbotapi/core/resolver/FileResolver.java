package com.ducnh.chatbotapi.core.resolver;

import com.ducnh.chatbotapi.constant.MediaType;
import com.ducnh.chatbotapi.core.BotDispatcher;
import com.ducnh.chatbotapi.model.BotCommand;
import com.ducnh.chatbotapi.model.BotCommandParams;
import com.ducnh.chatbotapi.utils.FileUtils;
import com.ducnh.chatbotapi.utils.SendMediaUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.File;

@Slf4j
public class FileResolver implements TypeResolver<File> {
    @Override
    public void resolve(File value, BotCommand command, BotCommandParams botCommandParams) {
        MediaType sendFile = command.getSendFile();
        SendMediaUtils.sendMedia(botCommandParams.getUpdate().getMessage(), FileUtils.getInputFile(value), botCommandParams.getUpdate().getMessage().getChatId(), sendFile, BotDispatcher.getInstance().getAbsSender());
        log.info("Reply Media: [{}]", sendFile);
    }

    @Override
    public Class<File> getType() {
        return File.class;
    }
}
