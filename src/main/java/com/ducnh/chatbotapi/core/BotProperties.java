package com.ducnh.chatbotapi.core;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@ConfigurationProperties(prefix = "ducnh.bot", ignoreUnknownFields = false)
public class BotProperties {

    private Boolean enableAutoConfig = true;
    private String username;
    private String token;
    private String loggingChatId;
    private List<String> botOwnerChatId = new ArrayList<>();
    private List<String> botRoutePackages = new ArrayList<>();
    private Boolean enableUpdateTrace = false;
    private Boolean disableDefaultCommands = false;
    private Executor executor = new Executor();
    private Integer registerDelay = 0;
    private Boolean showCommandMenu = true;
    private Webhook webhook = new Webhook();

    @Data
    @NoArgsConstructor
    public static class Executor {
        private int corePoolSize = 8;
        private int maxPoolSize = Integer.MAX_VALUE;
        private int queueCapacity = Integer.MAX_VALUE;
        private String threadNamePrefix = "bot-task-";
    }

    @Data
    @NoArgsConstructor
    public static class Webhook {
        private Boolean useWebhook = false;
        private String url;
        private String secretToken;
        private String baseCallbackPath;
    }

}
