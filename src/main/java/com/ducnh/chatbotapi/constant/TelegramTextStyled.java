package com.ducnh.chatbotapi.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
public enum TelegramTextStyled {
    BOLD("<b>", "</b>"),
    ITALIC("<i>", "</i>"),
    CODE("<code>", "</code>"),
    STRIKE("<s>", "</s>"),
    UNDERLINE("<u>", "</u>"),
    PRE("<pre>", "</pre>"),;
    private final String openTag;

    private final String closeTag;

    TelegramTextStyled(String openTag, String closeTag) {
        this.openTag = openTag;
        this.closeTag = closeTag;
    }
}
