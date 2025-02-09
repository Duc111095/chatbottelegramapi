package com.ducnh.chatbotapi;

import com.ducnh.chatbotapi.core.BotDispatcher;
import com.ducnh.chatbotapi.core.BotProperties;
import com.ducnh.chatbotapi.core.SimpleTelegramLongPollingCommandBot;
import com.ducnh.chatbotapi.core.SimpleTelegramWebhookCommandBot;
import com.ducnh.chatbotapi.core.processor.ProcessorConfig;
import com.ducnh.chatbotapi.core.registry.RegistryConfig;
import com.ducnh.chatbotapi.core.resolver.TypeResolverConfig;
import com.ducnh.chatbotapi.mapper.MapperConfig;
import com.ducnh.chatbotapi.repository.RepositoryConfig;
import com.ducnh.chatbotapi.subscriber.AfterRegisterBotSubscriber;
import com.ducnh.chatbotapi.subscriber.SubscriberConfig;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.context.event.EventListener;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.generics.Webhook;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import org.telegram.telegrambots.updatesreceivers.ServerlessWebhook;
import reactor.core.Exceptions;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Slf4j
@RequiredArgsConstructor
@AutoConfiguration
@ConditionalOnProperty(value = "ducnh.bot.enable-bot-config", havingValue = "true", matchIfMissing = true)
@ComponentScan
@EnableConfigurationProperties({BotProperties.class})
@Import({
        ProcessorConfig.class,
        RegistryConfig.class,
        TypeResolverConfig.class,
        SubscriberConfig.class,
        RepositoryConfig.class,
        MapperConfig.class
})
public class BotAutoConfiguration {
    private final BotProperties botProperties;
    private final ApplicationContext applicationContext;

    @Bean
    SimpleAsyncTaskExecutor botAsyncTaskExecutor() {
        log.info("Creating Default Bot Async Task Executor...");
        BotProperties.Executor executorProperties = botProperties.getExecutor();
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(executorProperties.getCorePoolSize());
        executor.setMaxPoolSize(executorProperties.getMaxPoolSize());
        executor.setQueueCapacity(executorProperties.getQueueCapacity());
        executor.setThreadNamePrefix(executorProperties.getThreadNamePrefix());
        return new SimpleAsyncTaskExecutor(executor);
    }

    @Bean
    @ConditionalOnProperty(value = "ducnh.bot.webhook.use-webhook", havingValue = "false", matchIfMissing = true)
    SimpleTelegramLongPollingCommandBot simpleTelegramLongPollingCommandBot() {
        return new SimpleTelegramLongPollingCommandBot(botProperties);
    }

    @Bean
    @ConditionalOnProperty(value = "ducnh.bot.webhook.use-webhook", havingValue = "true")
    SimpleTelegramWebhookCommandBot simpleTelegramWebhookCommandBot() {
        return new SimpleTelegramWebhookCommandBot(botProperties);
    }

    @Bean
    BotDispatcher botDispatcher(BotProperties botProperties) {
        boolean useWebhook = botProperties.getWebhook().getUseWebhook();
        AbsSender sender = useWebhook ? applicationContext.getBean(SimpleTelegramWebhookCommandBot.class) : applicationContext.getBean(SimpleTelegramLongPollingCommandBot.class);
        BotDispatcher.createInstance(applicationContext, botProperties, sender);
        return BotDispatcher.getInstance();
    }

    @Bean
    @ConditionalOnProperty(value = "ducnh.bot.webhook.use-webhook", havingValue = "true")
    SetWebhook setWebhook() {
        SetWebhook setWebhook = new SetWebhook();
        setWebhook.setUrl(botProperties.getWebhook().getUrl());
        setWebhook.setSecretToken(botProperties.getWebhook().getSecretToken());
        return setWebhook;
    }

    @Bean
    @ConditionalOnProperty(value = "ducnh.bot.webhook.use-webhook", havingValue = "true")
    ServerlessWebhook webhook() {
        return new ServerlessWebhook();
    }

    @SneakyThrows
    @EventListener(ApplicationReadyEvent.class)
    public void registerBot() {
        Mono.just(botProperties.getWebhook().getUseWebhook())
                .delaySubscription(Duration.ofSeconds(botProperties.getRegisterDelay()))
                .doOnSuccess( e -> {
                    log.info("Spring Boot Telegram Command Bot Auto Configuration by @nhduc");
                    applicationContext.getBean(AfterRegisterBotSubscriber.class)
                            .accept(BotDispatcher.getInstance().getAbsSender());
                })
                .doOnError( ex -> {
                    throw Exceptions.errorCallbackNotImplemented(ex);
                })
                .subscribe(this::registerBot);
    }

    @SneakyThrows
    private void registerBot(Boolean useWebhook) {
        if (useWebhook) {
            Webhook webhook = applicationContext.getBean(ServerlessWebhook.class);
            SimpleTelegramWebhookCommandBot bot = applicationContext.getBean(SimpleTelegramWebhookCommandBot.class);
            TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class, webhook);
            SetWebhook setWebhook = applicationContext.getBean(SetWebhook.class);
            telegramBotsApi.registerBot(bot, setWebhook);
        } else {
            SimpleTelegramLongPollingCommandBot bot = applicationContext.getBean(SimpleTelegramLongPollingCommandBot.class);
            TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
            telegramBotsApi.registerBot(bot);
        }
    }
}
