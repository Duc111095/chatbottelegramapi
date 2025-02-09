package com.ducnh.chatbotapi.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.telegram.telegrambots.meta.api.methods.ParseMode;

@RequiredArgsConstructor
public enum MessageParseMode {
    MARKDOWN(ParseMode.MARKDOWN),
    MARKDOWNV2(ParseMode.MARKDOWNV2),
    HTML(ParseMode.HTML),
    PLAIN("PLAIN"),
    ;
    @Getter
    private final String value;
}
