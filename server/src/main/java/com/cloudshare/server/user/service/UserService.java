package com.cloudshare.server.user.service;

import com.cloudshare.server.user.api.request.UserInfoRepDTO;
import com.cloudshare.server.user.api.request.UserLoginReqDTO;
import com.cloudshare.server.user.api.request.UserRegisterReqDTO;
import com.cloudshare.server.user.model.User;

/**
 * @author novo
 * @since 2023/10/5
 */
public interface UserService {
    Long register(UserRegisterReqDTO reqDTO);

    Boolean checkUsername(String username);

    String login(UserLoginReqDTO reqDTO);

    User findById(Long userId);

    UserInfoRepDTO getUserInfo();
}
