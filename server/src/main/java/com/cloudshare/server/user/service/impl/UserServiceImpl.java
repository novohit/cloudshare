package com.cloudshare.server.user.service.impl;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.crypto.SecureUtil;
import com.cloudshare.common.util.TokenUtil;
import com.cloudshare.lock.lock.ILock;
import com.cloudshare.server.auth.UserContext;
import com.cloudshare.server.auth.UserContextThreadHolder;
import com.cloudshare.server.user.api.request.UserInfoRepDTO;
import com.cloudshare.server.user.api.request.UserLoginReqDTO;
import com.cloudshare.server.user.api.request.UserRegisterReqDTO;
import com.cloudshare.server.user.model.User;
import com.cloudshare.server.user.repository.UserRepository;
import com.cloudshare.server.user.service.UserService;
import com.cloudshare.web.enums.BizCodeEnum;
import com.cloudshare.web.exception.BizException;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * @author novo
 * @since 2023/10/5
 */
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final ILock lock;

    public UserServiceImpl(UserRepository userRepository, ILock lock) {
        this.userRepository = userRepository;
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
                userContext.phone());
        return repDTO;
    }
}
