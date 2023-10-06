package com.cloudshare.server.user.service;

import com.cloudshare.server.user.api.request.UserRegisterReqDTO;

/**
 * @author novo
 * @since 2023/10/5
 */
public interface UserService {
    Long register(UserRegisterReqDTO reqDTO);

    Boolean checkUsername(String username);
}
