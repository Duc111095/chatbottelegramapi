package com.ducnh.chatbotapi.core.processor;

import com.ducnh.chatbotapi.core.registry.ResolverRegistry;
import com.ducnh.chatbotapi.core.resolver.TypeResolver;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.concurrent.Executor;

public class TypeResolverPostProcessor implements BeanPostProcessor, Ordered, BeanFactoryAware {
    private BeanFactory beanFactory;

    private boolean isTypeResolver(Object bean) {
        Class<?> clazz = AopUtils.getTargetClass(bean);
        return TypeResolver.class.isAssignableFrom(clazz);
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (isTypeResolver(bean)) {
            Mono.just(bean).subscribeOn(Schedulers.fromExecutor(getTaskExecutor())).subscribe( e -> beanFactory.getBean(ResolverRegistry.class).register((TypeResolver) e));
        }
        return bean;
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }
    private Executor getTaskExecutor() {
        return beanFactory.getBean("botAsyncTaskExecutor", SimpleAsyncTaskExecutor.class);
    }
}
