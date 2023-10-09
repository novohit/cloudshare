package com.cloudshare.server.file.controller.requset;

/**
 * @author novo
 * @since 2023/10/8
 */
public record FileListReqDTO(
        Long parentId,
        String curDirectory
) {
}
