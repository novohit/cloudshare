package com.cloudshare.server.file.controller.requset;

import javax.validation.constraints.NotBlank;

/**
 * @author novo
 * @since 2023/10/8
 */
public record FileListReqDTO(
        Long parentId,
        @NotBlank String curDirectory
) {
}
