package com.cloudshare.server.dto.requset;

import javax.validation.constraints.NotBlank;

/**
 * @author novo
 * @since 2023/10/5
 */
public record UserRegisterReqDTO(
        @NotBlank String username,
        @NotBlank String password,
        String avatar
) {
}
