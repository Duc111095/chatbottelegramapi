package com.ducnh.chatbotapi.core.resolver;

import com.ducnh.chatbotapi.model.BotCommand;
import com.ducnh.chatbotapi.model.BotCommandParams;
import com.ducnh.chatbotapi.subscriber.UpdateSubscriber;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.Collection;

@SuppressWarnings("rawtypes")
@Slf4j
public class CollectionResolver implements TypeResolver<Collection>, ApplicationContextAware {
    private ApplicationContext applicationContext;

    public CollectionResolver() {}

    @Override
    public void resolve(Collection value, BotCommand command, BotCommandParams botCommandParams) {
        UpdateSubscriber updateSubscriber = applicationContext.getBean(UpdateSubscriber.class);
        if (value != null && !value.isEmpty()) {
            value.forEach(e -> updateSubscriber.handleReturnedValue(() -> e, command, botCommandParams));
        } else {
            log.info("Nothing to reply. Cause return value(s) is empty collection/array");
        }
    }

    @Override
    public Class<Collection> getType() {
        return Collection.class;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
