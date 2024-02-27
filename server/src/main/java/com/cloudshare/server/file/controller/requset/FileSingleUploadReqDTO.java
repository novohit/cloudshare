package com.cloudshare.server.file.controller.requset;

import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author novo
 * @since 2023/10/11
 */
public record FileSingleUploadReqDTO(
        String md5,
        @NotNull Long parentId,
        @NotNull String curDirectory,
        @NotNull MultipartFile file
) {
}
