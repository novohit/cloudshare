package com.cloudshare.server.user.service;

import com.cloudshare.server.user.controller.request.UserInfoRepDTO;
import com.cloudshare.server.user.controller.request.UserLoginReqDTO;
import com.cloudshare.server.user.controller.request.UserRegisterReqDTO;
import com.cloudshare.server.user.controller.request.UserUpdateReqDTO;
import com.cloudshare.server.user.enums.LoginType;
import com.cloudshare.server.user.model.User;
import me.zhyd.oauth.model.AuthUser;

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

    String login(LoginType loginType, AuthUser authUser);

    void update(Long userId, UserUpdateReqDTO reqDTO);
}
