package com.ducnh.chatbotapi.route;

import com.ducnh.chatbotapi.annotations.BotRoute;
import com.ducnh.chatbotapi.annotations.CommandDescription;
import com.ducnh.chatbotapi.annotations.CommandMapping;
import com.ducnh.chatbotapi.mapper.UpdateMapper;
import com.ducnh.chatbotapi.repository.UpdateTraceRepository;
import com.ducnh.chatbotapi.utils.FileUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

@ConditionalOnProperty(value = "ducnh.bot.enable-update-trace", havingValue = "true")
@BotRoute
@Slf4j
public class TraceRoute {
    private final UpdateTraceRepository updateTraceRepository;
    private final UpdateMapper updateMapper;

    public TraceRoute(UpdateTraceRepository updateTraceRepository, @Qualifier("updateMapper") UpdateMapper updateMapper) {
        this.updateTraceRepository = updateTraceRepository;
        this.updateMapper = updateMapper;
    }

    @CommandDescription("Trace last 100 update incoming")
    @CommandMapping(value = "/update_trace", onlyForOwner = true)
    public Mono<InputFile> updateTrace(Update update) {
        return updateTraceRepository.fluxAll()
                .collectList()
                .map(updateMapper::writeValueAsPrettyString)
                .map(content -> FileUtils.getInputFile(content.getBytes(StandardCharsets.UTF_8), "trace.log"));
    }
}
