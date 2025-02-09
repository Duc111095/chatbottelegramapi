package com.ducnh.chatbotapi.mapper;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MapperConfig {
    @Bean(name="updateMapper")
    UpdateMapper updateMapper() {
        return new UpdateMapper();
    }
}
