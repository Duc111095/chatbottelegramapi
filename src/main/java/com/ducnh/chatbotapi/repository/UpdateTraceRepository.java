package com.ducnh.chatbotapi.repository;

import com.ducnh.chatbotapi.model.UpdateTrace;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface UpdateTraceRepository {
    void add(Mono<UpdateTrace> trace);

    void addAll(Flux<UpdateTrace> traces);

    Flux<UpdateTrace> fluxAll();
}
