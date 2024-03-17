package com.cloudshare.server.service.impl;

import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.crypto.SecureUtil;
import com.cloudshare.lock.lock.ILock;
import com.cloudshare.server.auth.UserContext;
import com.cloudshare.server.auth.UserContextThreadHolder;
import com.cloudshare.server.dto.requset.UserInfoRepVO;
import com.cloudshare.server.dto.requset.UserLoginReqDTO;
import com.cloudshare.server.dto.requset.UserRegisterReqDTO;
import com.cloudshare.server.dto.requset.UserUpdateReqDTO;
import com.cloudshare.server.converter.UserConverter;
import com.cloudshare.server.enums.LoginType;
import com.cloudshare.server.enums.PlanLevel;
import com.cloudshare.server.model.User;
import com.cloudshare.server.model.UserAuth;
import com.cloudshare.server.repository.UserAuthRepository;
import com.cloudshare.server.repository.UserRepository;
import com.cloudshare.server.service.UserService;
import com.cloudshare.web.enums.BizCodeEnum;
import com.cloudshare.web.exception.BizException;
import me.zhyd.oauth.model.AuthUser;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    private final UserConverter userConverter;

    public UserServiceImpl(UserRepository userRepository, UserAuthRepository userAuthRepository, ILock lock,
                           UserConverter userConverter) {
        this.userRepository = userRepository;
        this.userAuthRepository = userAuthRepository;
        this.lock = lock;
        this.userConverter = userConverter;
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
            user.setPlan(PlanLevel.FREE);
            user.setTotalQuota(PlanLevel.FREE.getQuota());
            user.setUsedQuota(0L);
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
            StpUtil.login(user.getId());
            SaTokenInfo tokenInfo = StpUtil.getTokenInfo();
            return tokenInfo.tokenValue;
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
    public UserInfoRepVO getUserInfo() {
        UserContext userContext = UserContextThreadHolder.getUserContext();
        UserInfoRepVO resp = userConverter.Context2VO(userContext);
        return resp;
    }

    @Override
    @Transactional
    public String login(LoginType loginType, AuthUser authUser) {
        Optional<UserAuth> optional = userAuthRepository.findByLoginTypeAndIdentify(loginType, authUser.getUuid());
        if (optional.isPresent()) {
            StpUtil.login(optional.get().getUserId());
            SaTokenInfo tokenInfo = StpUtil.getTokenInfo();
            return tokenInfo.tokenValue;
        } else {
            UserRegisterReqDTO reqDTO = new UserRegisterReqDTO(authUser.getUsername(), "123456", authUser.getAvatar());
            Long userId = register(reqDTO);
            UserAuth userAuth = new UserAuth(null, userId, LoginType.GOOGLE, authUser.getUuid());
            userAuthRepository.save(userAuth);
            StpUtil.login(userId);
            SaTokenInfo tokenInfo = StpUtil.getTokenInfo();
            return tokenInfo.tokenValue;
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

    @Override
    public void incrementQuota(Long fileSize, Long userId) {
        Optional<User> optional = userRepository.findById(userId);
        if (optional.isPresent()) {
            User user = optional.get();
            user.setUsedQuota(user.getUsedQuota() + fileSize);
            userRepository.save(user);
        }
    }

    @Override
    public void incrementTotalQuota(Long fileSize, Long userId) {
        Optional<User> optional = userRepository.findById(userId);
        if (optional.isPresent()) {
            User user = optional.get();
            user.setTotalQuota(user.getTotalQuota() + fileSize);
            userRepository.save(user);
        }
    }

    @Override
    public void updatePlan(PlanLevel planLevel, Long userId) {
        Optional<User> optional = userRepository.findById(userId);
        if (optional.isPresent()) {
            User user = optional.get();
            user.setPlan(planLevel);
            userRepository.save(user);
        }
    }
}
