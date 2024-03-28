package com.cloudshare.server.common.exception;

import org.springframework.http.HttpStatus;

import java.io.Serial;

/**
 * @author novo
 * @since 2023/10/9
 */
public class BadRequestException extends BizException {

    @Serial
    private static final long serialVersionUID = -8155081927087336984L;

    public BadRequestException(String message) {
        super(message);
        this.httpStatus = HttpStatus.BAD_REQUEST;
    }
}
