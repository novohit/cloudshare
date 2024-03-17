package com.cloudshare.server.dto.requset;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author novo
 * @since 2023/10/8
 */
public record DirCreateReqDTO(
        @NotNull Long parentId,
        @NotBlank String fileName,
        @NotNull String curDirectory
) {
}
