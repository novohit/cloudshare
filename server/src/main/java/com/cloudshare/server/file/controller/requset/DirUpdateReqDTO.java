package com.cloudshare.server.file.controller.requset;

import javax.validation.constraints.Positive;

/**
 * @author novo
 * @since 2023/10/9
 */
public record DirUpdateReqDTO(
        @Positive
        Long id,
        Long parentId,
        String name,
        String description,
        String curDirectory,
        String newDirectory
) {
}
