package com.ducnh.chatbotapi.core.resolver;

import com.ducnh.chatbotapi.annotations.ConditionalOnMissingTypeResolverBean;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ByteArrayResource;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.InputFile;

import java.io.File;
import java.util.Collection;

@SuppressWarnings("rawtypes")
public class TypeResolverConfig {

    @Bean
    @ConditionalOnMissingTypeResolverBean(BotApiMethod.class)
    TypeResolver<BotApiMethod> botApiMethodTypeResolver() {
        return new BotApiMethodResolver();
    }

    @Bean
    @ConditionalOnMissingTypeResolverBean(byte[].class)
    TypeResolver<byte[]> byteTypeResolver() {
        return new ByteArrayResolver();
    }

    @Bean
    @ConditionalOnMissingTypeResolverBean(ByteArrayResource.class)
    TypeResolver<ByteArrayResource> byteArrayResourceTypeResolver() {
        return new ByteArrayResourceResolver();
    }

    @Bean
    @ConditionalOnMissingTypeResolverBean(File.class)
    TypeResolver<File> fileTypeResolver() {
        return new FileResolver();
    }

    @Bean
    @ConditionalOnMissingTypeResolverBean(InputFile.class)
    TypeResolver<InputFile> inputFileTypeResolver() {
        return new InputFileResolver();
    }

    @Bean
    @ConditionalOnMissingTypeResolverBean(Number.class)
    TypeResolver<Number> numberResolver() {
        return new NumberResolver();
    }

    @Bean
    @ConditionalOnMissingTypeResolverBean(String.class)
    TypeResolver<String> stringTypeResolver() {
        return new StringResolver();
    }

    @Bean
    @ConditionalOnMissingTypeResolverBean(Collection.class)
    TypeResolver<Collection> collectionTypeResolver() {
        return new CollectionResolver();
    }
}
