package com.cloudshare.server.file.api.requset;

/**
 * @author novo
 * @since 2023/10/8
 */
public record DirAddReqDTO(
        Long parentId,
        String name,
        String curDirectory
) {
}
