package com.ducnh.chatbotapi.subscriber;

import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.function.BiConsumer;

public interface CommandNotFoundUpdateSubscriber extends BiConsumer<Update, String> {
}
