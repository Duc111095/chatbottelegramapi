package com.ducnh.chatbotapi.repository.impl;

import com.ducnh.chatbotapi.model.UpdateTrace;
import com.ducnh.chatbotapi.repository.UpdateTraceRepository;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Repository
public class InMemoryTraceRepository implements UpdateTraceRepository, ApplicationContextAware {
    private ApplicationContext applicationContext;
    private int capacity = 100;
    private boolean reserve = true;
    private final List<UpdateTrace> traces = new CopyOnWriteArrayList<>();

    public void setReserve(boolean reserve) {
        synchronized (this.traces) {
            this.reserve = reserve;
        }
    }

    public void setCapacity(int capacity) {
        synchronized (this.traces) {
            this.capacity = capacity;
        }
    }

    private void subscribe(UpdateTrace updateTrace) {
        while (this.traces.size() >= this.capacity) {
            this.traces.remove(this.reserve ? this.capacity - 1 : 0);
        }
        if (this.reserve) {
            this.traces.add(0, updateTrace);
        } else {
            this.traces.add(updateTrace);
        }
    }

    @Override
    public void add(Mono<UpdateTrace> trace) {
        trace.subscribeOn(Schedulers.fromExecutor(applicationContext.getBean("botAsyncTaskExecutor", SimpleAsyncTaskExecutor.class))).subscribe(this::subscribe);
    }

    @Override
    public void addAll(Flux<UpdateTrace> traces) {
        traces.subscribeOn(Schedulers.fromExecutor(applicationContext.getBean("botAsyncTaskExecutor", SimpleAsyncTaskExecutor.class))).subscribe(this::subscribe);
    }

    @Override
    public Flux<UpdateTrace> fluxAll() {
        return Flux.fromStream(this.traces.stream());
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
