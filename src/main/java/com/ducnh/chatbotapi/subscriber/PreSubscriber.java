package com.ducnh.chatbotapi.subscriber;

import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.function.Consumer;

public interface PreSubscriber extends Consumer<Update> {
}
