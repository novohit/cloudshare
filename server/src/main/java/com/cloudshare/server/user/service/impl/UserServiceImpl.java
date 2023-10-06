package com.cloudshare.server.user.service.impl;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.crypto.SecureUtil;
import com.cloudshare.lock.lock.ILock;
import com.cloudshare.server.user.api.request.UserRegisterReqDTO;
import com.cloudshare.server.user.model.User;
import com.cloudshare.server.user.repository.UserRepository;
import com.cloudshare.server.user.service.UserService;
import com.cloudshare.web.exception.BizException;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

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
            throw new BizException("用户名已存在");
        }
        try {
            userRepository.findByUsername(reqDTO.username()).ifPresent(user -> {
                throw new BizException("用户名已存在");
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
            throw new BizException("用户名已存在");
        });
        return true;
    }
}
