package com.cloudshare.server.dto.requset;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author novo
 * @since 2023/11/1
 */
public record ShareSaveReqDTO(
        @NotNull List<Long> sources,
        @NotNull Long targetId,
        @NotNull String target // 目标目录
) {
}
