package com.cloudshare.server.auth.config;

import com.cloudshare.server.auth.interceptor.LoginInterceptor;
import com.cloudshare.server.auth.interceptor.ShareInterceptor;
import com.cloudshare.server.share.service.ShareService;
import com.cloudshare.server.user.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;
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

    private final ShareService shareService;

    public WebConfiguration(UserService userService, ShareService shareService) {
        this.userService = userService;
        this.shareService = shareService;
    }

    @Bean
    public LoginInterceptor loginInterceptor() {
        return new LoginInterceptor(userService);
    }

    @Bean
    public ShareInterceptor shareInterceptor() {
        return new ShareInterceptor(shareService);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginInterceptor())
                .excludePathPatterns(getExcludePath());
        registry.addInterceptor(shareInterceptor());
    }

    private List<String> getExcludePath() {
        List<String> exclude = Stream.of(
                        "/oauth/**",
                        "/user/**/register",
                        "/user/**/login",
                        "/user/**/captcha",
                        "/user/**/send-code",
                        "/pay/callback/**",
                        "/share/check-code",
                        "/share/access",
                        "/share/sharer",
                        "/**/test*"
                )
                .map(uri -> "/api" + uri)
                .collect(Collectors.toList());
        exclude.add("/*");
        exclude.add("/img/**");
        return exclude;
    }

    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        // 配置只应用于RestController注解的类
        configurer.addPathPrefix("/api", aClass -> aClass.isAnnotationPresent(RestController.class));
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 静态资源---图片url地址
        registry.addResourceHandler("/img/**").addResourceLocations("file:" + System.getProperty("user.dir") + File.separator + "temp" + File.separator);
    }
}
