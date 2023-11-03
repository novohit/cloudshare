package com.cloudshare.web.exception;

import org.springframework.http.HttpStatus;

/**
 * @author novo
 * @since 2023/10/9
 */
public class BadRequestException extends BizException {

    public BadRequestException(String message) {
        super(message);
        this.httpStatus = HttpStatus.BAD_REQUEST;
    }
}
