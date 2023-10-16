package com.cloudshare.server.file.controller.requset;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;

/**
 * @author novo
 * @since 2023/10/13
 */
public record FileChunkMergeReqDTO(
        @NotBlank String md5,
        @Positive Long parentId,
        @NotBlank String curDirectory,
        @NotBlank String fileName
) {
}
