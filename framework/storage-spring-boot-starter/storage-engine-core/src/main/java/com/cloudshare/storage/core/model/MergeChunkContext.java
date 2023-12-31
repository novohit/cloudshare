package com.cloudshare.storage.core.model;

import lombok.Data;

import java.util.List;

/**
 * @author novo
 * @since 2023/10/16
 */
@Data
public class MergeChunkContext {

    /**
     * 分片合并所需要的信息
     */
    private List<String> chunkInfoList;

    /**
     * 文件名带后缀
     */
    private String fileName;

    /**
     * 上传后回传真实路径
     */
    private String realPath;
}
