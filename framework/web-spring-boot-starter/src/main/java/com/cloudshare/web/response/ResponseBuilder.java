package com.cloudshare.web.response;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * @author novo
 * @since 2023/10/4
 */
public class ResponseBuilder {

    public static ResponseEntity<Response<Void>> success() {
        Response<Void> response = Response.success();
        return ResponseEntity.ok().body(response);
    }


    public static <T> ResponseEntity<Response<T>> success(T data) {
        Response<T> response = Response.success(data);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    public static ResponseEntity<Response<Void>> error(HttpStatus httpStatus, String message) {
        Response<Void> response = Response.error(message);
        return ResponseEntity.status(httpStatus).body(response);
    }

}

