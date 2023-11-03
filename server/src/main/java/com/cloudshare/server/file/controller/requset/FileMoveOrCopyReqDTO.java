package com.cloudshare.server.file.controller.requset;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author novo
 * @since 2023/11/1
 */
public record FileMoveOrCopyReqDTO(
        @NotNull Long parentId,
        @NotNull List<Long> fileIds,
        @NotBlank String target // 目标目录
) {
}
