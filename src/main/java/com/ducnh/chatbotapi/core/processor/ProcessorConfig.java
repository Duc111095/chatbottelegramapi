package com.ducnh.chatbotapi.core.processor;

import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

import java.util.Arrays;
import java.util.List;

public class ProcessorConfig implements ImportBeanDefinitionRegistrar {
    private static final List<Class<? extends BeanPostProcessor>> BEANPOST_PROCESSOR_LIST = Arrays.asList(
            BotRoutePostProcessor.class,
            BotAdvicePostProcessor.class,
            TypeResolverPostProcessor.class
    );

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        BEANPOST_PROCESSOR_LIST.forEach(processorClass -> registerBean(registry, processorClass.getName(), processorClass));
    }

    private <T> void registerBean(BeanDefinitionRegistry registry, String beanName, Class<T> clazz) {
        if (!registry.containsBeanDefinition(beanName)) {
            registry.registerBeanDefinition(beanName, new RootBeanDefinition(clazz));
        }
    }
}
