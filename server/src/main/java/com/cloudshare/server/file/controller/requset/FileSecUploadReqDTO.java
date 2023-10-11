package com.cloudshare.server.file.controller.requset;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;

/**
 * @author novo
 * @since 2023/10/11
 */
public record FileSecUploadReqDTO(
        @NotBlank String fileName,
        @NotBlank String md5,
        @Positive Long parentId,
        @NotBlank String curDirectory
) {
}
