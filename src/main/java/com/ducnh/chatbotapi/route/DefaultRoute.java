package com.ducnh.chatbotapi.route;

import com.ducnh.chatbotapi.annotations.BotRoute;
import com.ducnh.chatbotapi.annotations.ChatId;
import com.ducnh.chatbotapi.annotations.CommandDescription;
import com.ducnh.chatbotapi.annotations.CommandMapping;
import com.ducnh.chatbotapi.constant.CommonConstant;
import com.ducnh.chatbotapi.constant.MediaType;
import com.ducnh.chatbotapi.constant.MessageParseMode;
import com.ducnh.chatbotapi.constant.TelegramTextStyled;
import com.ducnh.chatbotapi.core.BotDispatcher;
import com.ducnh.chatbotapi.core.BotProperties;
import com.ducnh.chatbotapi.model.BotCommand;
import com.ducnh.chatbotapi.utils.FileUtils;
import com.ducnh.chatbotapi.utils.TelegramMessageUtils;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.telegram.telegrambots.meta.api.objects.Update;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.File;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@ConditionalOnProperty(value = "ducnh.bot.disable-default-commands", havingValue = "false", matchIfMissing = true)
@BotRoute
@Slf4j
@RequiredArgsConstructor
public class DefaultRoute {
    private final BotDispatcher botDispatcher;
    private final BotProperties botProperties;

    @Value("${logging.file.name:#{null}")
    private String logFile;

    private String described(BotCommand botCommand, boolean isMessageInGroup) {
        StringBuilder result = new StringBuilder();
        StringBuilder cmd = new StringBuilder(botCommand.getCmd());
        if (StringUtils.isNotBlank(botCommand.getBodyDescription())) {
            cmd.append(" [").append(botCommand.getBodyDescription()).append("]");
        }
        result.append(TelegramMessageUtils.wrapByTag(cmd.toString(), TelegramTextStyled.CODE));
        if (StringUtils.isNotBlank(botCommand.getDescription())) {
            result.append(": ").append(botCommand.getDescription());
        }
        if (isMessageInGroup && botCommand.isOnlyAdmin()) {
            result.append(" (").append("only admin group has permission").append(")");
        }
        return result.toString();
    }

    @CommandDescription(CommonConstant.HELP_CMD_DESCRIPTION)
    @CommandMapping(value = CommonConstant.HELP_CMD, allowAllUserAccess = true, parseMode = MessageParseMode.HTML)
    public Mono<String> getCmdByChat(Update update, @ChatId Long chatId) {
        boolean isMessageInGroup = TelegramMessageUtils.isMessageInGroup(update.getMessage());
        String title = TelegramMessageUtils.wrapByTag("List of available commands for this chat: ", TelegramTextStyled.BOLD);
        AtomicInteger index = new AtomicInteger(0);
        List<String> result = botDispatcher.getAvailableBotCommands(update)
                .stream()
                .map(botCommand -> described(botCommand, isMessageInGroup))
                .sorted()
                .map(described -> index.getAndIncrement() + ". " + described)
                .toList();
        return Flux.merge(Flux.just(title), Flux.fromIterable(result))
                .collect(Collectors.joining(System.lineSeparator()));
    }

    @CommandDescription(CommonConstant.START_CMD_DESCRIPTION)
    @CommandMapping(value = CommonConstant.START_CMD, allowAllUserAccess = true)
    public String start(Update update) {
        return String.format("Hi, %s. Please use /help to know all I can do", update.getMessage().getFrom().getFirstName());
    }

    @CommandDescription(CommonConstant.GET_LOG_FILE_CMD_DESCRIPTION)
    @CommandMapping(value = CommonConstant.GET_LOG_FILE_CMD, sendFile = MediaType.DOCUMENT, onlyForOwner = true)
    public Object getLog(Update update, @ChatId Long chatId) {
        if (botProperties.getBotOwnerChatId().contains(String.valueOf(chatId))) {
            if (StringUtils.isNotBlank(logFile)) {
                return FileUtils.getInputFile(new File(logFile));
            }
            else {
                return "Please config a property logging.file.name to get log file with this cmd!";
            }
        }
        return null;
    }
}
