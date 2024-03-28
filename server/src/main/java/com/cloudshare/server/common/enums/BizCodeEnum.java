package com.cloudshare.server.common.enums;

import lombok.Getter;

public enum BizCodeEnum {

    SUCCESS(0, "操作成功"),

    SERVER_ERROR(1, "系统异常"),

    USER_REPEAT(250001, "用户名已经存在"),

    USER_NOT_EXIST(250002, "用户不存在"),

    USER_LOGIN_ERROR(250003, "用户名或者密码错误"),

    USER_UNAUTHORIZED(250004, "用户未登录"),

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
