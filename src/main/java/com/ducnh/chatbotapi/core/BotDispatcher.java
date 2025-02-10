package com.ducnh.chatbotapi.core;

import com.ducnh.chatbotapi.constant.ChatMemberStatus;
import com.ducnh.chatbotapi.constant.CommonConstant;
import com.ducnh.chatbotapi.core.registry.CommandRegistry;
import com.ducnh.chatbotapi.exception.BotAccessDeniedException;
import com.ducnh.chatbotapi.model.BotCommand;
import com.ducnh.chatbotapi.model.BotCommandParams;
import com.ducnh.chatbotapi.model.MessageParser;
import com.ducnh.chatbotapi.subscriber.UpdateSubscriber;
import com.ducnh.chatbotapi.utils.TelegramMessageUtils;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationContext;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.groupadministration.GetChatMember;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.chatmember.ChatMember;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.bots.AbsSender;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public final class BotDispatcher {
    private final ApplicationContext applicationContext;
    private final BotProperties botProperties;

    @Getter
    private final AbsSender absSender;

    @Getter
    private static BotDispatcher instance;

    private BotDispatcher(ApplicationContext applicationContext, BotProperties botProperties, AbsSender sender) {
        this.applicationContext = applicationContext;
        this.botProperties = botProperties;
        this.absSender = sender;
    }

    public static void createInstance(ApplicationContext applicationContext, BotProperties botProperties, AbsSender sender) {
        synchronized (BotDispatcher.class) {
            if (instance == null) {
                instance = new BotDispatcher(applicationContext, botProperties, sender);
            } else {
                throw new UnsupportedOperationException("This is a singleton class and cannot be instantiated more than once");
            }
        }
    }

    public UpdateSubscriber getUpdateSubscriber() {
        return applicationContext.getBean(UpdateSubscriber.class);
    }

    public CommandRegistry getCommandRegistry() {
        return applicationContext.getBean(CommandRegistry.class);
    }

    private boolean hasPermission(Update update, BotCommand botCommand) {
        try {
            Message message = update.getMessage();
            Long chatId = message.getChatId();
            Long userSendId = message.getFrom().getId();
            boolean isMessageInGroup = TelegramMessageUtils.isMessageInGroup(message);
            if (isMessageInGroup && (botCommand.isOnlyForOwner()) || botCommand.isOnlyForPrivate()) {
                return false;
            }
            else if (isMessageInGroup && botCommand.isOnlyAdmin()) {
                GetChatMember getChatMember = new GetChatMember(chatId + "", userSendId);
                ChatMember chatMember = this.executeSneakyThrows(getChatMember);
                ChatMemberStatus status = ChatMemberStatus.fromStatusString(chatMember.getStatus());
                return status == ChatMemberStatus.ADMINISTRATOR || status == ChatMemberStatus.CREATOR;
            }
            else if (isMessageInGroup) {
                boolean isAcceptedGroup = botCommand.isAllowAllGroupsAccess() || Arrays.stream(botCommand.getAccessGroupIds()).anyMatch(e -> e == chatId);
                boolean isAcceptedMember = botCommand.getAccessMemberIds().length == 0 || Arrays.stream(botCommand.getAccessMemberIds()).anyMatch(e -> e == userSendId);
                return botCommand.isAllowAllUsersAccess() || (isAcceptedGroup && isAcceptedMember);
            } else if (botCommand.isOnlyForGroup()) {
                return false;
            } else if (botCommand.isOnlyForOwner()) {
                return botProperties.getBotOwnerChatId().contains(String.valueOf(userSendId));
            } else if (botCommand.isAllowAllUsersAccess()) {
                return true;
            }
            return Arrays.stream(botCommand.getAccessUserIds()).anyMatch(e -> e == userSendId);
        } catch (Exception e) {
            log.error("Error: {}", e.getMessage(), e);
        }
        return false;
    }

    public List<BotCommand> getAvailableBotCommands(Update update) {
        return getCommandRegistry().getCommands()
                .stream()
                .filter(botCommand -> this.hasPermission(update, botCommand))
                .collect(Collectors.toList());
    }

    @SneakyThrows
    private String truncatedBotUsername(String command) {
        String postfix = "@" + absSender.getMe().getUserName();
        if (StringUtils.endsWith(command, postfix)) {
            return command.substring(0, command.length() - postfix.length());
        }
        return command;
    }

    public Optional<BotCommand> getCommand(Update update) {
        CommandRegistry commandRegistry = getCommandRegistry();
        BotCommand botCommand = null;
        Message message = update.getMessage();
        MessageParser messageParser = new MessageParser(message.getText());
        String truncatedCommand = truncatedBotUsername(messageParser.getFirstWord());
        if (commandRegistry.hasCommand(truncatedCommand)) {
            if (hasPermission(update, commandRegistry.getCommand(truncatedCommand))) {
                botCommand = commandRegistry.getCommand(truncatedCommand);
            }
            else if (Boolean.TRUE.equals(botProperties.getShowCommandMenu()))
                throw new BotAccessDeniedException(CommonConstant.ACCESS_DENIED_MESSAGE);
        }
        return Optional.ofNullable(botCommand);
    }

    public BotCommandParams getCommandParameters(Update update) {
        Message message = update.getMessage();
        if (message.hasText()) {
            Long chatId = message.getChatId();
            MessageParser messageParser = new MessageParser(message.getText());
            return BotCommandParams.builder()
                    .withUpdate(update)
                    .withMessage(message)
                    .withCmdBody(messageParser.getRemainingText())
                    .withSendUserId(message.getFrom().getId())
                    .withSendUsername(Objects.toString(update.getMessage().getFrom().getUserName(), ""))
                    .withChatId(chatId)
                    .build();
        }
        else if (message.hasPhoto() || message.hasDocument()) {
            return getCommandParamsWithMedia(update);
        }
        return null;
    }

    private BotCommandParams getCommandParamsWithMedia(Update update) {
        Message message = update.getMessage();
        MessageParser messageParser = new MessageParser(message.getText());
        Long chatId = message.getChatId();
        return BotCommandParams.builder()
                .withUpdate(update)
                .withMessage(message)
                .withCmdBody(messageParser.getRemainingText())
                .withSendUserId(message.getFrom().getId())
                .withSendUsername(Objects.toString(update.getMessage().getFrom().getUserName(), ""))
                .withChatId(chatId)
                .withPhotoSizes(message.getPhoto())
                .withDocument(message.getDocument())
                .build();
    }

    @SneakyThrows
    public void onRegisterBot() {
        SetMyCommands setMyCommands;
        if (Boolean.TRUE.equals(botProperties.getShowCommandMenu())) {
            List<org.telegram.telegrambots.meta.api.objects.commands.BotCommand> commandList =
                    getCommandRegistry().getCommands()
                            .stream()
                            .sorted((e1, e2) -> StringUtils.compare(e1.getCmd(), e2.getCmd()))
                            .map(e -> {
                                StringBuilder description = new StringBuilder();
                                if (StringUtils.isNotBlank(e.getDescription())) {
                                    description.append(e.getDescription());
                                }
                                if (StringUtils.isNotBlank(e.getBodyDescription())) {
                                    description.append(" [").append(e.getBodyDescription()).append("]");
                                }
                                if (description.isEmpty()) {
                                    description.append(e.getCmd().replace(CommonConstant.CMD_PREFIX, ""));
                                }
                                return new org.telegram.telegrambots.meta.api.objects.commands.BotCommand(e.getCmd(), description.toString());
                            })
                            .toList();
            setMyCommands = new SetMyCommands(commandList, new BotCommandScopeDefault(), null);
        } else {
            setMyCommands = new SetMyCommands(Collections.singletonList(CommonConstant.HELP_BOT_COMMAND), new BotCommandScopeDefault(), null);
        }
        absSender.execute(setMyCommands);
        log.info("Bot {} has started successfully", botProperties.getUsername());
    }

    @SneakyThrows
    public <T extends Serializable, M extends BotApiMethod<T>> T executeSneakyThrows(M method) {
        return absSender.execute(method);
    }
}
