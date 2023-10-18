package com.cloudshare.server.file.controller.requset;

import com.cloudshare.server.file.enums.FileType;

import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * @author novo
 * @since 2023/10/8
 */
public record FileListReqDTO(
        Long parentId,
        @NotBlank String curDirectory,
        List<FileType> fileTypeList
) {
}
