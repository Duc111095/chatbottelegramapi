package com.ducnh.chatbotapi.exception;

public class BotAccessDeniedException extends RuntimeException {
    public BotAccessDeniedException(String message) {
        super(message);
    }
    public BotAccessDeniedException(Throwable throwable) {
        super(throwable);
    }
    public BotAccessDeniedException() {
        super();
    }
}
