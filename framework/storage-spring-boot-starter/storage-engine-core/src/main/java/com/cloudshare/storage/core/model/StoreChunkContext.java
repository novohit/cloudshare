package com.cloudshare.storage.core.model;

import lombok.Data;

import java.io.InputStream;

/**
 * @author novo
 * @since 2023/10/13
 */
@Data
public class StoreChunkContext {

    /**
     * 文件名带后缀
     */
    private String fileName;


    /**
     * 分片大小
     */
    private Long totalSize;


    private InputStream inputStream;

    /**
     * 分片信息
     */
    private String chunkInfo;

    /**
     * 分片序号
     */
    private Integer chunkNum;

    private String md5;
}
