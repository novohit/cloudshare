package com.cloudshare.server.search.controller.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author novo
 * @since 2023/11/9
 */
public record SearchReqDTO(
        @NotNull Long parentId,
        @NotBlank String curDirectory,
        String keyword
) {
}
