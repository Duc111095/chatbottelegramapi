package com.ducnh.chatbotapi.model;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

@Getter
public class MessageParser {
    private final String firstWord;
    private final String remainingText;

    public MessageParser(String text) {
        if (StringUtils.isNotBlank(text)) {
            String[] arr = text.split(" ");
            String command = arr[0];
            String body = "";
            if (arr.length > 1) {
                body = text.split(command + " ")[1];
            }
            this.firstWord = command;
            this.remainingText = body;
        }
        else {
            this.firstWord = "";
            this.remainingText = "";
        }
    }
}
