package com.cloudshare.server.service;

import com.cloudshare.server.dto.requset.UserInfoRepVO;
import com.cloudshare.server.dto.requset.UserListPageReqDTO;
import com.cloudshare.server.dto.requset.UserLoginReqDTO;
import com.cloudshare.server.dto.requset.UserRegisterReqDTO;
import com.cloudshare.server.dto.requset.UserUpdateReqDTO;
import com.cloudshare.server.dto.response.PageResponse;
import com.cloudshare.server.enums.LoginType;
import com.cloudshare.server.enums.PlanLevel;
import com.cloudshare.server.model.User;
import me.zhyd.oauth.model.AuthUser;

import java.util.List;

/**
 * @author novo
 * @since 2023/10/5
 */
public interface UserService {
    Long register(UserRegisterReqDTO reqDTO);

    Boolean checkUsername(String username);

    String login(UserLoginReqDTO reqDTO);

    User findById(Long userId);

    UserInfoRepVO getUserInfo();

    String login(LoginType loginType, AuthUser authUser);

    void update(Long userId, UserUpdateReqDTO reqDTO);

    void incrementQuota(Long fileSize, Long userId);

    void incrementTotalQuota(Long fileSize, Long userId);

    void updatePlan(PlanLevel planLevel, Long userId);

    PageResponse<User> list(UserListPageReqDTO reqDTO);
}
