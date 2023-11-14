package org.novo.limit.exception;

import java.io.Serial;

/**
 * @author novo
 * @since 2023/11/14
 */
public class RateLimitException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 7364848679991344873L;

    public RateLimitException(String message) {
        super(message);
    }
}
