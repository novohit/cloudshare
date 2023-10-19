package com.cloudshare.server.file.controller.requset;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;

/**
 * @author novo
 * @since 2023/10/9
 */
public record FileRenameReqDTO(
        @Positive Long id,
        @NotBlank String oldName,
        @NotBlank String newName,
        String curDirectory
) {
}
