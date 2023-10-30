package com.cloudshare.storage.core.model;

import lombok.Data;

import java.io.InputStream;

/**
 * @author novo
 * @since 2023/10/13
 */
@Data
public class StoreChunkContext {

    private String fileNameWithSuffix;


    private Long totalSize;


    private InputStream inputStream;

    /**
     * 分片信息
     */
    private String chunkInfo;

    /**
     * 分片序号
     */
    private Integer chunk;

    private String md5;
}
