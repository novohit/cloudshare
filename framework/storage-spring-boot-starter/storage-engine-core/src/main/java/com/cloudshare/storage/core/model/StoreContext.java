package com.cloudshare.storage.core.model;

import lombok.Data;

import java.io.InputStream;

/**
 * @author novo
 * @since 2023/10/10
 */
@Data
public class StoreContext {


    private String fileNameWithSuffix;


    private Long totalSize;


    private InputStream inputStream;

    /**
     * 上传后回传真实路径
     */
    private String realPath;
}
