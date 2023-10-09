package com.cloudshare.server.user.controller;

import com.cloudshare.server.user.controller.request.UserInfoRepDTO;
import com.cloudshare.server.user.controller.request.UserLoginReqDTO;
import com.cloudshare.server.user.controller.request.UserRegisterReqDTO;
import com.cloudshare.server.user.service.UserService;
import com.cloudshare.web.response.Response;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotBlank;

/**
 * @author novo
 * @since 2023/10/5
 */
@RestController
@RequestMapping("/user")
@Validated
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/check-username")
    public Response<Boolean> checkUsername(@RequestParam("username") @NotBlank String username) {
        Boolean valid = userService.checkUsername(username);
        return Response.success(valid);
    }

    @GetMapping
    public Response<UserInfoRepDTO> getUserInfo() {
        UserInfoRepDTO repDTO = userService.getUserInfo();
        return Response.success(repDTO);
    }

    @PostMapping("/register")
    public Response<Long> register(@Validated @RequestBody UserRegisterReqDTO reqDTO) {
        Long userId = userService.register(reqDTO);
        return Response.success(userId);
    }

    @PostMapping("/login")
    public Response<String> login(@Validated @RequestBody UserLoginReqDTO reqDTO) {
        String accessToken = userService.login(reqDTO);
        return Response.success(accessToken);
    }
}
