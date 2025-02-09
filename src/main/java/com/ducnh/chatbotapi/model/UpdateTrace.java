package com.ducnh.chatbotapi.model;

import lombok.Getter;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Calendar;
import java.util.Date;

@Getter
public class UpdateTrace {
    private final Date timestamp;
    private final long startNanoTime;
    private final Update update;
    public UpdateTrace(Update update) {
        this.update = update;
        this.startNanoTime = System.nanoTime();
        this.timestamp = Calendar.getInstance().getTime();
    }
}
