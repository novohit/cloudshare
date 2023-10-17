package com.cloudshare.storage.core.model;

import lombok.Data;

import java.io.OutputStream;

/**
 * @author novo
 * @since 2023/10/17
 */
@Data
public class ReadContext {

    private String realPath;

    /**
     * 回传二进制流
     */
    private OutputStream outputStream;
}
