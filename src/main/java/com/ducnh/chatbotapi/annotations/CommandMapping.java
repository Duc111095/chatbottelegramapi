package com.ducnh.chatbotapi.annotations;

import com.ducnh.chatbotapi.constant.MediaType;
import com.ducnh.chatbotapi.constant.MessageParseMode;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface CommandMapping {
    String[] value();
    MessageParseMode parseMode() default MessageParseMode.PLAIN;
    boolean disabledWebPagePreview() default false;
    long[] accessUserIds() default {};
    long[] accessMemberIds() default {};
    long[] accessGroupIds() default {};
    boolean allowAllUserAccess() default false;
    boolean allowAllGroupAccess() default false;
    boolean onlyAdmin() default false;
    boolean onlyForGroup() default false;
    boolean onlyForPrivate() default false;
    MediaType sendFile() default MediaType.DOCUMENT;
    boolean onlyForOwner() default false;
}
