package com.ducnh.chatbotapi.repository;

import com.ducnh.chatbotapi.repository.impl.InMemoryTraceRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RepositoryConfig {

    @ConditionalOnProperty(value = "ducnh.bot.enable-update-trace", havingValue = "true")
    @ConditionalOnMissingBean(UpdateTraceRepository.class)
    @Bean
    UpdateTraceRepository updateTraceRepository() {
        return new InMemoryTraceRepository();
    }
}
