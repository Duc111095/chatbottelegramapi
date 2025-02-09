package com.ducnh.chatbotapi.model;

import com.ducnh.chatbotapi.annotations.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.telegram.telegrambots.meta.api.objects.Document;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

@Data
@Builder(setterPrefix = "with")
@AllArgsConstructor
public class BotCommandParams {

    @TypeArg
    private Update update;

    @TypeArg
    private Message message;

    @AnnotationArg(CommandBody.class)
    private String cmdBody;

    @AnnotationArg(SendUserId.class)
    private Long sendUserId;

    @AnnotationArg(SendUsername.class)
    private String sendUsername;

    @AnnotationArg(ChatId.class)
    private Long chatId;

    @TypeArg
    private List<PhotoSize> photoSizes;

    @TypeArg
    private Document document;

    @AnnotationArg(CommandName.class)
    private String commandName;
}
