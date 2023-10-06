package com.cloudshare.server.user.api.request;

import javax.validation.constraints.NotBlank;

/**
 * @author novo
 * @since 2023/10/5
 */
public record UserRegisterReqDTO(
        @NotBlank String username,
        @NotBlank String password
) {
}
