package com.cloudshare.storage.core.model;

import lombok.Data;

import java.util.List;

/**
 * @author novo
 * @since 2023/10/16
 */
@Data
public class MergeChunkContext {

    private List<String> chunkRealPathList;

    private String fileNameWithSuffix;

    /**
     * 上传后回传真实路径
     */
    private String realPath;
}
