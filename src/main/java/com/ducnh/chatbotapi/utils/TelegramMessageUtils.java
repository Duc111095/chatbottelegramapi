package com.ducnh.chatbotapi.utils;

import com.ducnh.chatbotapi.constant.MessageParseMode;
import com.ducnh.chatbotapi.constant.TelegramTextStyled;
import jakarta.annotation.Nullable;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.bots.AbsSender;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@UtilityClass
public class TelegramMessageUtils {
    public static String wrapByTag(String raw, TelegramTextStyled styled) {
        return styled.getOpenTag() + raw + styled.getCloseTag();
    }

    @SneakyThrows
    public static void replyMessage(AbsSender bot, Message messageToReply, String replyContent, MessageParseMode parseMode, boolean disableWebPagePreview) {
        replyMessage(bot, messageToReply.getChatId() + "", messageToReply.getMessageId(), replyContent, parseMode, disableWebPagePreview);
    }

    @SneakyThrows
    public static void replyMessage(AbsSender bot, String chatId, @Nullable Integer messageId, String replyContent, MessageParseMode parseMode, boolean disableWebPagePreview) {
        SendMessage sendMessage = new SendMessage();
        if (parseMode != null && parseMode != MessageParseMode.PLAIN) {
            sendMessage.setParseMode(parseMode.getValue());
        }
        sendMessage.setChatId(chatId);
        sendMessage.setText(replyContent);
        if (disableWebPagePreview) {
            sendMessage.setDisableWebPagePreview(true);
        }
        if (messageId != null) {
            sendMessage.setReplyToMessageId(messageId);
        }
        bot.execute(sendMessage);
    }

    public static void replyMessage(AbsSender bot, Message messageToReply, String replyContent, MessageParseMode parseMode) {
        replyMessage(bot, messageToReply, replyContent, parseMode, false);
    }

    public static boolean isMessageInGroup(Message received) {
        return StringUtils.equalsAny(received.getChat().getType(), "group", "supergroup");
    }

    public static boolean isChannelPost(Update update) {
        return update.getChannelPost() != null && update.getChannelPost().getText() != null && !update.getChannelPost().getText().isEmpty();
    }

    public static List<String> lineWrap(String text, int width, boolean shiftNewLines) {
        String[] words = text.trim().split("\\s+");
        StringBuilder currentLine = new StringBuilder();
        List<String> lines = new ArrayList<>();
        int currentLength = 0;
        for (int i = 0; i < words.length; i++) {
            currentLine.append(words[i]).append(" ");
            currentLength = currentLine.length();

            int nextWordLength = 0;
            if (i + 1 < words.length) {
                nextWordLength = words[i + 1].length();
            }
            if (currentLength + nextWordLength >= width - 2 || i + 1 >= words.length) {
                lines.add(currentLine.toString());
                currentLine = new StringBuilder();
                if (shiftNewLines) {
                    currentLine.append(" ");
                }
            }
        }
        return lines;
    }
}
