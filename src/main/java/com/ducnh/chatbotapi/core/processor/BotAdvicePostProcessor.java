package com.ducnh.chatbotapi.core.processor;

import com.ducnh.chatbotapi.annotations.BotExceptionHandler;
import com.ducnh.chatbotapi.annotations.BotRouteAdvice;
import com.ducnh.chatbotapi.core.registry.AdviceRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.MethodIntrospector;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import reactor.core.Exceptions;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;

@Slf4j
public class BotAdvicePostProcessor implements BeanPostProcessor, SmartInitializingSingleton, Ordered, BeanFactoryAware {
    private BeanFactory beanFactory;
    private final Set<Class<?>> nonAnnotatedClasses = Collections.newSetFromMap(new ConcurrentHashMap<>(64));

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if (!this.nonAnnotatedClasses.contains(bean.getClass())) {
            Class<?> targetClass = AopUtils.getTargetClass(bean);
            BotRouteAdvice botRouteAdvice = AnnotationUtils.findAnnotation(targetClass, BotRouteAdvice.class);
            if (botRouteAdvice != null) {
                Map<Method, BotExceptionHandler> annotatedMethods = MethodIntrospector.selectMethods(targetClass,
                        (MethodIntrospector.MetadataLookup<BotExceptionHandler>) method -> AnnotationUtils.findAnnotation(method, BotExceptionHandler.class));
                if (annotatedMethods.isEmpty()) {
                    this.nonAnnotatedClasses.add(bean.getClass());
                    log.trace("No @BotExceptionHandler annotations found on bean type: {}", bean.getClass());
                }
                else {
                    Flux.fromIterable(annotatedMethods.entrySet())
                            .doOnError(ex -> {
                                throw Exceptions.errorCallbackNotImplemented(ex);
                            })
                            .doAfterTerminate(() -> log.debug("{} @BotExceptionHandler methods processed on bean '{}': {}", annotatedMethods.size(), beanName, annotatedMethods))
                            .subscribeOn(Schedulers.fromExecutor(getTaskExecutor()))
                            .subscribe(entry -> {
                                Method method = entry.getKey();
                                BotExceptionHandler botExceptionHandler = entry.getValue();
                                beanFactory.getBean(AdviceRegistry.class).register(botExceptionHandler.value(), method, bean);
                            });
                }
            }

        }
        else {
            nonAnnotatedClasses.add(bean.getClass());
        }
        return bean;
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    @Override
    public void afterSingletonsInstantiated() {
        nonAnnotatedClasses.clear();
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }

    private Executor getTaskExecutor() {
        return beanFactory.getBean("botAsyncTaskExecutor", SimpleAsyncTaskExecutor.class);
    }

}
