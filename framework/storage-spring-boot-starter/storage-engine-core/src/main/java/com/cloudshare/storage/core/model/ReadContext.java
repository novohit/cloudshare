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
     * 读取位置
     */
    private long position;

    /**
     * 要读取的总字节数
     */
    private long size;

    /**
     * 回传二进制流
     */
    private OutputStream outputStream;
}
