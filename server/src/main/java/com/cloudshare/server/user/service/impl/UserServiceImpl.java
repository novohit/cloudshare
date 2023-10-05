package com.cloudshare.server.user.service.impl;

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

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Long register(UserRegisterReqDTO reqDTO) {
        userRepository.findByUsername(reqDTO.username()).ifPresent(user -> {
            throw new BizException("用户名已存在");
        });
        User user = new User();
        BeanUtils.copyProperties(reqDTO, user);
        user.setSalt("test");
        userRepository.save(user);
        return user.getId();
    }
}
