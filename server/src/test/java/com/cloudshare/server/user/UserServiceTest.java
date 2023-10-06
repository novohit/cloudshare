package com.cloudshare.server.user;

import com.cloudshare.server.user.api.request.UserRegisterReqDTO;
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
    void registerTest() {
        UserRegisterReqDTO reqDTO = new UserRegisterReqDTO("dev", "test");
        Long userId = userService.register(reqDTO);
        Assertions.assertNotNull(userId);
        Assertions.assertThrows(
                BizException.class,
                () -> userService.register(reqDTO)
        );
    }
}
