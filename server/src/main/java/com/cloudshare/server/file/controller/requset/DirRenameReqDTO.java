package com.cloudshare.server.file.controller.requset;

import javax.validation.constraints.Positive;

/**
 * @author novo
 * @since 2023/10/9
 */
public record DirRenameReqDTO(
        @Positive
        Long id,
        String oldName,
        String newName,
        String curDirectory
) {
}
