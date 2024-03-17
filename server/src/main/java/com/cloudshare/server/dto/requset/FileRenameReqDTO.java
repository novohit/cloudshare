package com.cloudshare.server.dto.requset;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author novo
 * @since 2023/10/9
 */
public record FileRenameReqDTO(
        @NotNull Long fileId,
        @NotBlank String oldName,
        @NotBlank String newName,
        String curDirectory
) {
}
