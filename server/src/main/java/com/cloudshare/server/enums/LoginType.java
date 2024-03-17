package com.cloudshare.server.enums;

import lombok.Getter;

import java.util.Arrays;

/**
 * @author novo
 * @since 2023/10/6
 */
@Getter
public enum LoginType {

    GOOGLE("google"),

    GITHUB("github"),

    ;

    private final String desc;

    LoginType(String desc) {
        this.desc = desc;
    }

    public static LoginType fromString(String text) {
        return Arrays.stream(LoginType.values())
                .filter(loginType -> loginType.desc.equalsIgnoreCase(text))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No enum constant for " + text));
    }
}
