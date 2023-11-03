package com.cloudshare.web.exception;

import org.springframework.http.HttpStatus;

/**
 * @author novo
 * @since 2023/10/31
 */
public class ShareTokenMissingException extends BizException {

    public ShareTokenMissingException(String message) {
        super(message);
        this.httpStatus = HttpStatus.OK;
    }
}
