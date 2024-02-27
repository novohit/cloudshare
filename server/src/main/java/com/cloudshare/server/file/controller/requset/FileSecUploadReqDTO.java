package com.cloudshare.server.file.controller.requset;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author novo
 * @since 2023/10/11
 */
public record FileSecUploadReqDTO(
        @NotBlank String fileName,
        @NotBlank String md5,
        @NotNull Long parentId,
        @NotNull String curDirectory
) {
}
