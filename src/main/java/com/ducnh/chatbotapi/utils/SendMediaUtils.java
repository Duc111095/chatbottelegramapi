package com.ducnh.chatbotapi.utils;

import com.ducnh.chatbotapi.constant.MediaType;
import jakarta.annotation.Nullable;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.send.SendSticker;
import org.telegram.telegrambots.meta.api.methods.send.SendVoice;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.bots.AbsSender;
import reactor.function.Consumer4;

import java.util.EnumMap;
import java.util.Map;

@Slf4j
@UtilityClass
public class SendMediaUtils {
    private static final Map<MediaType, Consumer4<Message, InputFile, Long, AbsSender>> inputfileConsumers = new EnumMap<>(MediaType.class);

    static {
        inputfileConsumers.put(MediaType.STICKER, SendMediaUtils::sendSticker);
        inputfileConsumers.put(MediaType.DOCUMENT, SendMediaUtils::sendDocument);
        inputfileConsumers.put(MediaType.PHOTO, SendMediaUtils::sendPhoto);
        inputfileConsumers.put(MediaType.VOICE, SendMediaUtils::sendVoice);

    }

    @SneakyThrows
    public static void sendVoice(@Nullable Message messageToReply, InputFile inputFile, Long chatId, AbsSender bot) {
        SendVoice sendVoice = new SendVoice();
        sendVoice.setVoice(inputFile);
        sendVoice.setChatId(String.valueOf(chatId));
        if (messageToReply != null) {
            sendVoice.setReplyToMessageId(messageToReply.getMessageId());
        }
        bot.execute(sendVoice);
    }

    @SneakyThrows
    public static void sendPhoto(@Nullable Message messageToReply, InputFile inputFile, Long chatId, AbsSender bot){
        SendPhoto sendPhoto = new SendPhoto();
        sendPhoto.setPhoto(inputFile);
        sendPhoto.setChatId(String.valueOf(chatId));
        if (messageToReply != null) {
            sendPhoto.setReplyToMessageId(messageToReply.getMessageId());
        }
        bot.execute(sendPhoto);
    }

    @SneakyThrows
    public static void sendDocument(@Nullable Message messageToReply, InputFile inputFile, Long chatId, AbsSender bot) {
        SendDocument sendDocument = new SendDocument();
        sendDocument.setDocument(inputFile);
        if (messageToReply != null) {
            sendDocument.setReplyToMessageId(messageToReply.getMessageId());
        }
        sendDocument.setChatId(String.valueOf(chatId));
        bot.execute(sendDocument);
    }

    @SneakyThrows
    public static void sendSticker(@Nullable Message messageToReply, InputFile sticker, Long chatId, AbsSender bot) {
        SendSticker sendSticker = new SendSticker();
        sendSticker.setSticker(sticker);
        sendSticker.setChatId(String.valueOf(chatId));
        if (messageToReply != null) {
            sendSticker.setReplyToMessageId(messageToReply.getMessageId());
        }
        bot.execute(sendSticker);
    }

    public static void sendMedia(@Nullable Message messageToReply, InputFile inputFile, Long chatId, MediaType mediaType, AbsSender bot) {
        if (inputfileConsumers.containsKey(mediaType)) {
            inputfileConsumers.get(mediaType).accept(messageToReply, inputFile, chatId, bot);
        }
    }
}
