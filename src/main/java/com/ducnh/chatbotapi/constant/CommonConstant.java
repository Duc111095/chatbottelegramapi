package com.ducnh.chatbotapi.constant;

import lombok.experimental.UtilityClass;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;

import java.util.regex.Pattern;

@UtilityClass
public class CommonConstant {
    public static final String CMD_PREFIX = "/";
    public static final String CMD_PREFIX_ERROR = "Command must be start with %s";
    public static final int CMD_MAX_LENGTH = 32;
    public static final String CMD_MAX_LENGTH_ERROR = "Command cannot be longer than %d (including %s)";
    public static final Pattern CMD_PATTERN = Pattern.compile("^[a-z0-9_]*$");
    public static final String CMD_PATTERN_ERROR = "Command must contain only lowercase English letters, digits and underscores.";
    public static final String CMD_BLANK_ERROR = "Command cannot be null or empty or blank";
    public static final String ERROR_NOTIFY_MESSAGE = "There is an error. Please contact bot owner to check it";
    public static final String HELP_CMD = CMD_PREFIX + "help";
    public static final String HELP_CMD_DESCRIPTION = "List of available command(s) for this chat";
    public static final String START_CMD = CMD_PREFIX + "start";
    public static final String START_CMD_DESCRIPTION = "Start chat with a bot";
    public static final String GET_LOG_FILE_CMD = CMD_PREFIX + "get_log_file";
    public static final String GET_LOG_FILE_CMD_DESCRIPTION = "Get an application log file";
    public static final BotCommand HELP_BOT_COMMAND = new BotCommand(HELP_CMD, HELP_CMD_DESCRIPTION);
    public static final int MAX_MESSAGE_CONTENT_LENGTH = 4096;
    public static final String TEMP_PREFIX = "temp_";
    public static final String ACCESS_DENIED_MESSAGE = "403. You do not have permission to call this command";

}
