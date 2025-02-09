package com.ducnh.chatbotapi.core.registry;

import org.springframework.context.annotation.Bean;

public class RegistryConfig {
    @Bean
    CommandRegistry commandRegistry() {
        return new CommandRegistry();
    }

    @Bean
    AdviceRegistry adviceRegistry() {
        return new AdviceRegistry();
    }

    @Bean
    ResolverRegistry resolverRegistry() {
        return new ResolverRegistry();
    }
}
