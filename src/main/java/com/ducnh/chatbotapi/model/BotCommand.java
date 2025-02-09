package com.ducnh.chatbotapi.model;

import com.ducnh.chatbotapi.constant.MediaType;
import com.ducnh.chatbotapi.constant.MessageParseMode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;

@Slf4j
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder(setterPrefix = "with")
public class BotCommand {
    private String cmd;
    private MessageParseMode parseMode;
    private boolean disableWebPagePreview;
    private long[] accessUserIds;
    private long[] accessMemberIds;
    private long[] accessGroupIds;
    private boolean allowAllUsersAccess;
    private boolean allowAllGroupsAccess;
    private boolean onlyForGroup;
    private boolean onlyForPrivate;
    private boolean onlyAdmin;
    private MediaType sendFile;
    private boolean onlyForOwner;
    private Method method;
    private String description;
    private String bodyDescription;
}
