package com.cloudshare.server.user.service.impl;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.crypto.SecureUtil;
import com.cloudshare.common.util.TokenUtil;
import com.cloudshare.lock.lock.ILock;
import com.cloudshare.server.auth.UserContext;
import com.cloudshare.server.auth.UserContextThreadHolder;
import com.cloudshare.server.user.controller.request.UserInfoRepDTO;
import com.cloudshare.server.user.controller.request.UserLoginReqDTO;
import com.cloudshare.server.user.controller.request.UserRegisterReqDTO;
import com.cloudshare.server.user.controller.request.UserUpdateReqDTO;
import com.cloudshare.server.user.enums.LoginType;
import com.cloudshare.server.user.model.User;
import com.cloudshare.server.user.model.UserAuth;
import com.cloudshare.server.user.repository.UserAuthRepository;
import com.cloudshare.server.user.repository.UserRepository;
import com.cloudshare.server.user.service.UserService;
import com.cloudshare.web.enums.BizCodeEnum;
import com.cloudshare.web.exception.BizException;
import me.zhyd.oauth.model.AuthUser;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.Optional;

/**
 * @author novo
 * @since 2023/10/5
 */
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final UserAuthRepository userAuthRepository;

    private final ILock lock;

    public UserServiceImpl(UserRepository userRepository, UserAuthRepository userAuthRepository, ILock lock) {
        this.userRepository = userRepository;
        this.userAuthRepository = userAuthRepository;
        this.lock = lock;
    }

    @Override
    public Long register(UserRegisterReqDTO reqDTO) {
        if (!lock.tryLock(reqDTO.username(), 30)) {
            throw new BizException(BizCodeEnum.USER_REPEAT);
        }
        try {
            userRepository.findByUsername(reqDTO.username()).ifPresent(user -> {
                throw new BizException(BizCodeEnum.USER_REPEAT);
            });
            User user = new User();
            BeanUtils.copyProperties(reqDTO, user);
            String salt = RandomUtil.randomString(8);
            String cryptPassword = SecureUtil.md5(salt + reqDTO.password());
            user.setSalt(salt);
            user.setPassword(cryptPassword);
            userRepository.save(user);
            return user.getId();
        } finally {
            lock.unlock(reqDTO.username());
        }
    }

    @Override
    public Boolean checkUsername(String username) {
        userRepository.findByUsername(username).ifPresent(user -> {
            throw new BizException(BizCodeEnum.USER_REPEAT);
        });
        return true;
    }

    @Override
    public String login(UserLoginReqDTO reqDTO) {
        Optional<User> optional = userRepository.findByUsername(reqDTO.username());
        if (optional.isPresent()) {
            User user = optional.get();
            String cryptPassword = SecureUtil.md5(user.getSalt() + reqDTO.password());
            if (!cryptPassword.equals(user.getPassword())) {
                throw new BizException(BizCodeEnum.USER_LOGIN_ERROR);
            }
            return TokenUtil.generateAccessToken(user.getId());
        }
        throw new BizException(BizCodeEnum.USER_LOGIN_ERROR);
    }

    @Override
    public User findById(Long userId) {
        Optional<User> optional = userRepository.findById(userId);
        if (optional.isEmpty()) {
            throw new BizException(BizCodeEnum.USER_NOT_EXIST);
        }
        return optional.get();
    }

    @Override
    public UserInfoRepDTO getUserInfo() {
        UserContext userContext = UserContextThreadHolder.getUserContext();
        UserInfoRepDTO repDTO = new UserInfoRepDTO(
                userContext.id(),
                userContext.username(),
                userContext.phone(),
                userContext.avatar(),
                0L,
                "/");
        return repDTO;
    }

    @Override
    @Transactional
    public String login(LoginType loginType, AuthUser authUser) {
        Optional<UserAuth> optional = userAuthRepository.findByLoginTypeAndIdentify(loginType, authUser.getUuid());
        if (optional.isPresent()) {
            return TokenUtil.generateAccessToken(optional.get().getUserId());
        } else {
            UserRegisterReqDTO reqDTO = new UserRegisterReqDTO(authUser.getUsername(), "123456", authUser.getAvatar());
            Long userId = register(reqDTO);
            UserAuth userAuth = new UserAuth(null, userId, LoginType.GOOGLE, authUser.getUuid());
            userAuthRepository.save(userAuth);
            return TokenUtil.generateAccessToken(userId);
        }
    }

    @Override
    public void update(Long userId, UserUpdateReqDTO reqDTO) {
        if (!UserContextThreadHolder.getUserId().equals(userId)) {
            return;
        }
        Optional<User> optional = userRepository.findById(userId);
        if (optional.isPresent()) {
            User user = optional.get();
            user.setAvatar(reqDTO.avatar());
            userRepository.save(user);
        }
    }
}
