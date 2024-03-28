package com.cloudshare.server.common.response;

import com.cloudshare.server.common.enums.BizCodeEnum;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;

import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;

/**
 * @author novo
 * @since 2023-02-20
 */
@Getter
@JsonInclude(value = JsonInclude.Include.NON_NULL) // 如果json的data为null 不返回给前端
public class Response<T> implements Serializable {

    @Serial
    private static final long serialVersionUID = -6606268589712484106L;

    private final Integer code;

    private final T data;

    private final String message;

    private final String requestId;

    private Response(BizCodeEnum bizCodeEnum, T data) {
        this.code = bizCodeEnum.getCode();
        this.message = bizCodeEnum.getMessage();
        this.data = data;
        this.requestId = UUID.randomUUID().toString();
    }

    private Response(int code, T data, String message) {
        this.code = code;
        this.message = message;
        this.data = data;
        this.requestId = UUID.randomUUID().toString();
    }

    public static Response<Void> success() {
        return new Response<>(BizCodeEnum.SUCCESS, null);
    }


    public static <T> Response<T> success(T data) {
        return new Response<>(BizCodeEnum.SUCCESS, data);
    }


    public static Response<Void> error(String message) {
        return new Response<>(BizCodeEnum.SERVER_ERROR.getCode(), null, message);
    }

    public static Response<Void> build(BizCodeEnum bizCodeEnum) {
        return new Response<>(bizCodeEnum.getCode(), null, bizCodeEnum.getMessage());
    }

    public static <T> Response<T> build(int code, T data, String message) {
        return new Response<>(code, data, message);
    }
}
