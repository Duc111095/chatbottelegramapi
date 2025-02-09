package com.ducnh.chatbotapi.core.resolver;

import com.ducnh.chatbotapi.constant.MediaType;
import com.ducnh.chatbotapi.core.BotDispatcher;
import com.ducnh.chatbotapi.model.BotCommand;
import com.ducnh.chatbotapi.model.BotCommandParams;
import com.ducnh.chatbotapi.utils.FileUtils;
import com.ducnh.chatbotapi.utils.SendMediaUtils;
import lombok.extern.slf4j.Slf4j;

import java.time.ZoneId;

@Slf4j
public class ByteArrayResolver implements TypeResolver<byte[]> {
    @Override
    public void resolve(byte[] value, BotCommand command, BotCommandParams botCommandParams) {
        MediaType sendFile = command.getSendFile();
        SendMediaUtils.sendMedia(botCommandParams.getUpdate().getMessage(), FileUtils.getInputFile(value, "temp_" + FileUtils.getPostfixFileInstantByTime(ZoneId.systemDefault())), botCommandParams.getUpdate().getMessage().getChatId(), sendFile, BotDispatcher.getInstance().getAbsSender());
        log.debug("Reply Media: [{}]", sendFile);
    }

    @Override
    public Class<byte[]> getType() {
        return byte[].class;
    }
}
