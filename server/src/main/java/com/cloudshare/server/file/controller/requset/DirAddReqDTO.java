package com.cloudshare.server.file.controller.requset;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

/**
 * @author novo
 * @since 2023/10/8
 */
public record DirAddReqDTO(
        @NotNull Long parentId,
        @NotBlank String dirName,
        @NotBlank String curDirectory
) {
}
