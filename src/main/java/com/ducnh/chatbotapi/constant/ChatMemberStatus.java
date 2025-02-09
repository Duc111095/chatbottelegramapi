package com.ducnh.chatbotapi.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;

@RequiredArgsConstructor
@Getter
public enum ChatMemberStatus {
    CREATOR("creator"),
    ADMINISTRATOR("administrator"),
    MEMBER("member"),
    LEFT("left"),
    KICKED("kicked"),;
    private final String value;
    public static ChatMemberStatus fromStatusString(String value) {
        return Arrays.stream(values())
                .filter(v -> StringUtils.equalsIgnoreCase(v.getValue(), value))
                .findFirst()
                .orElse(null);
    }
}
