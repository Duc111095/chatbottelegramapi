package com.ducnh.chatbotapi.subscriber;

import com.ducnh.chatbotapi.subscriber.impl.*;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

public class SubscriberConfig {

    @ConditionalOnMissingBean(NonCommandUpdateSubscriber.class)
    @Bean
    NonCommandUpdateSubscriber defaultNonCommandUpdateSubscriber() {
        return new DefaultNonCommandUpdateSubscriber();
    }

    @ConditionalOnMissingBean(CommandNotFoundUpdateSubscriber.class)
    @Bean
    CommandNotFoundUpdateSubscriber commandNotFoundUpdateSubscriber() {
        return new DefaultCommandNotFoundUpdateSubscriber();
    }

    @ConditionalOnMissingBean(CallbackQuerySubscriber.class)
    @Bean
    CallbackQuerySubscriber defaultCallbackQuerySubscriber() {
        return new DefaultCallbackQuerySubscriber();
    }

    @ConditionalOnMissingBean(PosSubscriber.class)
    @Bean
    PosSubscriber defaultPosSubscriber() {
        return new DefaultPosSubscriber();
    }

    @ConditionalOnMissingBean(PreSubscriber.class)
    @Bean
    PreSubscriber defaultPreSubscriber() {
        return new DefaultPreSubscriber();
    }

    @ConditionalOnMissingBean(AfterRegisterBotSubscriber.class)
    @Bean
    AfterRegisterBotSubscriber defaultAfterRegisterBotSubscriber() {
        return new DefaultAfterRegisterBotSubscriber();
    }

    @Bean
    UpdateSubscriber updateSubscriber() {
        return new UpdateSubscriber();
    }

}
