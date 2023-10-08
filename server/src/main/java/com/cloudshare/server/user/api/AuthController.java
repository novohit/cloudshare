package com.cloudshare.server.user.api;

import com.cloudshare.server.user.enums.LoginType;
import com.cloudshare.server.user.service.UserService;
import com.xkcoding.http.config.HttpConfig;
import me.zhyd.oauth.config.AuthConfig;
import me.zhyd.oauth.model.AuthCallback;
import me.zhyd.oauth.model.AuthResponse;
import me.zhyd.oauth.model.AuthUser;
import me.zhyd.oauth.request.AuthGoogleRequest;
import me.zhyd.oauth.request.AuthRequest;
import me.zhyd.oauth.utils.AuthStateUtils;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;

/**
 * @author novo
 * @since 2023/10/6
 */
@RestController
@RequestMapping("/oauth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/render/{source}")
    public void renderAuth(@PathVariable("source") String source, HttpServletResponse response) throws IOException {
        AuthRequest authRequest = getAuthRequest(LoginType.fromString(source));
        response.sendRedirect(authRequest.authorize(AuthStateUtils.createState()));
    }

    @GetMapping("/callback/{source}")
    @SuppressWarnings("unchecked")
    public void login(@PathVariable("source") String source, AuthCallback callback, HttpServletResponse response) {
        LoginType loginType = LoginType.fromString(source);
        AuthRequest authRequest = getAuthRequest(loginType);
        AuthResponse<AuthUser> authResponse = authRequest.login(callback);
        String accessToken = userService.login(loginType, authResponse.getData());
        response.setHeader("Location", "http://localhost:5173?token=" + accessToken);
//        return Response.success(accessToken);
        response.setStatus(HttpStatus.FOUND.value());
    }

    private AuthRequest getAuthRequest(LoginType loginType) {
        switch (loginType) {
            case GOOGLE -> {
                return new AuthGoogleRequest(AuthConfig.builder()
                        .clientId("342994534137-ras735k0pju62e0inefok56p8muvg50h.apps.googleusercontent.com")
                        .clientSecret("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")
                        .redirectUri("http://localhost:8080/api/oauth/callback/google")
                        // 针对国外平台配置代理
                        .httpConfig(HttpConfig.builder()
                                .timeout(15000)
                                // host 和 port 请修改为开发环境的参数
                                .proxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress("127.0.0.1", 7890)))
                                .build())
                        .build());
            }
            case GITHUB -> {
                return null;
            }
            default -> {
                return null;
            }
        }
    }
}
