package com.cloudshare.server.dto.requset;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author novo
 * @since 2023/11/1
 */
public record FileMoveOrCopyReqDTO(
        @NotNull Long parentId, // 目标
        @NotNull List<Long> fileIds,
        @NotNull String target // 目标目录
) {
}
