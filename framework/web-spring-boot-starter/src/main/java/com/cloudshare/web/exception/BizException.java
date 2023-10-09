package com.cloudshare.web.exception;

import com.cloudshare.web.enums.BizCodeEnum;
import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * @author novo
 * @since  2023-02-21
 */
@Getter
public class BizException extends RuntimeException {

    protected Integer code;

    protected String message;

    protected HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;

    public BizException(String message) {
        super(message);
        this.code = -1;
        this.message = message;
    }

    public BizException(BizCodeEnum bizCodeEnum) {
        super(bizCodeEnum.getMessage());
        this.code = bizCodeEnum.getCode();
        this.message = bizCodeEnum.getMessage();
    }

    public BizException(BizCodeEnum bizCodeEnum, HttpStatus httpStatus) {
        super(bizCodeEnum.getMessage());
        this.code = bizCodeEnum.getCode();
        this.message = bizCodeEnum.getMessage();
        this.httpStatus = httpStatus;
    }
}
