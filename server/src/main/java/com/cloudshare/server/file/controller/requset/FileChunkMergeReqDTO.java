package com.cloudshare.server.file.controller.requset;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author novo
 * @since 2023/10/13
 */
public record FileChunkMergeReqDTO(
        @NotBlank String md5,
        @NotNull Long parentId,
        @NotBlank String curDirectory,
        @NotBlank String fileName
) {
}
