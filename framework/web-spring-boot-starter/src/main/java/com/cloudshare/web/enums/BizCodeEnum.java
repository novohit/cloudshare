package com.cloudshare.web.enums;

import lombok.Getter;

public enum BizCodeEnum {

    SUCCESS(0, "操作成功"),

    SERVER_ERROR(1, "系统异常"),

    ;

    @Getter
    private final String message;

    @Getter
    private final int code;

    BizCodeEnum(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
