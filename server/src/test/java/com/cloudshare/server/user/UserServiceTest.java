package com.cloudshare.server.user;

import com.cloudshare.server.user.controller.request.UserLoginReqDTO;
import com.cloudshare.server.user.controller.request.UserRegisterReqDTO;
import com.cloudshare.server.user.service.UserService;
import com.cloudshare.web.exception.BizException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author novo
 * @since 2023/10/6
 */
@SpringBootTest
@Transactional
public class UserServiceTest {

    @Autowired
    private UserService userService;

    @Test
    void authTest() {
        UserRegisterReqDTO registerReqDTO = new UserRegisterReqDTO("dev", "test", "");
        Long userId = userService.register(registerReqDTO);
        Assertions.assertNotNull(userId);
        // 重复注册
        Assertions.assertThrows(
                BizException.class,
                () -> userService.register(registerReqDTO)
        );

        // 用户名密码正确
        UserLoginReqDTO rightLogin = new UserLoginReqDTO(registerReqDTO.username(), registerReqDTO.password());
        String accessToken = userService.login(rightLogin);
        Assertions.assertNotNull(accessToken);

        // 密码不正确
        UserLoginReqDTO passwordError = new UserLoginReqDTO(registerReqDTO.username(), registerReqDTO.password() + " ");
        Assertions.assertThrows(
                BizException.class,
                () -> userService.login(passwordError)
        );

        // 用户名不正确
        UserLoginReqDTO usernameError = new UserLoginReqDTO(registerReqDTO.username() + " ", registerReqDTO.password());
        Assertions.assertThrows(
                BizException.class,
                () -> userService.login(usernameError)
        );
    }
}
