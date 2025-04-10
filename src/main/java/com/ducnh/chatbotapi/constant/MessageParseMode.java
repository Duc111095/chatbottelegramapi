package com.ducnh.chatbotapi.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.telegram.telegrambots.meta.api.methods.ParseMode;

public enum MessageParseMode {
    MARKDOWN(ParseMode.MARKDOWN),
    MARKDOWNV2(ParseMode.MARKDOWNV2),
    HTML(ParseMode.HTML),
    PLAIN("PLAIN"),
    ;
    private final String value;
    MessageParseMode(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
