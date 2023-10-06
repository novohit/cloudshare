package com.cloudshare.server.auth.config;

import com.cloudshare.server.auth.interceptor.LoginInterceptor;
import com.cloudshare.server.user.repository.UserRepository;
import com.cloudshare.server.user.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author novo
 * @since 2023-09-21
 */
@Configuration
@ConditionalOnClass(DispatcherServlet.class) // equal @ConditionalOnWebApplication
@Slf4j
public class WebConfiguration implements WebMvcConfigurer {

    private final UserService userService;

    public WebConfiguration(UserService userService) {
        this.userService = userService;
    }

    @Bean
    public LoginInterceptor loginInterceptor() {
        return new LoginInterceptor(userService);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginInterceptor())
                .excludePathPatterns(getExcludePath());
    }

    private List<String> getExcludePath() {
        List<String> exclude = Stream.of(
                        "/oauth/**",
                        "/user/**/register",
                        "/user/**/login",
                        "/user/**/captcha",
                        "/user/**/send-code",
                        "/pay/callback/**",
                        "/**/test*"
                )
                .map(uri -> "/api" + uri)
                .collect(Collectors.toList());
        exclude.add("/");
        return exclude;
    }

    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        // 配置只应用于RestController注解的类
        configurer.addPathPrefix("/api", aClass -> aClass.isAnnotationPresent(RestController.class));
    }
}
