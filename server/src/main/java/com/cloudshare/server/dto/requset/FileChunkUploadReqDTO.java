package com.cloudshare.server.dto.requset;

import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

/**
 * @author novo
 * @since 2023/10/13
 */
public record FileChunkUploadReqDTO(
        @NotBlank String fileName,
        @NotBlank String md5,
        @Positive Integer chunkNum, // 分片序号
        @Positive Integer totalChunkSize, // 分片数
        Long totalSize, // 文件总大小 分上传边合并才会用到
        @NotNull MultipartFile file
) {
}
