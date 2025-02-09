package com.ducnh.chatbotapi.mapper;

import org.springframework.context.annotation.Bean;

public class MapperConfig {
    @Bean(name="updateMapper")
    UpdateMapper updateMapper() {
        return new UpdateMapper();
    }
}
