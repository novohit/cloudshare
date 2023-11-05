package com.cloudshare.server.auth.interceptor;

import com.cloudshare.common.util.TokenUtil;
import com.cloudshare.server.auth.UserContext;
import com.cloudshare.server.auth.UserContextThreadHolder;
import com.cloudshare.server.user.model.User;
import com.cloudshare.server.user.service.UserService;
import com.cloudshare.web.enums.BizCodeEnum;
import com.cloudshare.web.exception.BizException;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 登录拦截器
 *
 * @author novo
 * @since 2023-03-07
 */
@Slf4j
public class LoginInterceptor implements HandlerInterceptor {

    private final UserService userService;

    public final static String AUTHORIZATION_HEADER = "Authorization";

    public final static String BEARER = "Bearer";

    public LoginInterceptor(UserService userService) {
        this.userService = userService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String authorization = request.getHeader(AUTHORIZATION_HEADER);
        // token为空
        if (!StringUtils.hasText(authorization)) {
            authorization = request.getParameter(AUTHORIZATION_HEADER);
            if (!StringUtils.hasText(authorization)) {
                log.info("token为空");
                throw new BizException(BizCodeEnum.USER_UNAUTHORIZED, HttpStatus.UNAUTHORIZED);
            }
        }
        // token格式不正确
        if (!authorization.startsWith(BEARER)) {
            log.info("token格式错误");
            throw new BizException(BizCodeEnum.USER_UNAUTHORIZED, HttpStatus.UNAUTHORIZED);

        }
        String[] tokens = authorization.split(" ");
        // 避免数组越界
        if (tokens.length != 2) {
            log.info("token格式错误");
            throw new BizException(BizCodeEnum.USER_UNAUTHORIZED, HttpStatus.UNAUTHORIZED);
        }
        // 校验
        Claims claims = TokenUtil.verifyToken(tokens[1]);
        if (claims == null) {
            log.info("token不合法");
            throw new BizException(BizCodeEnum.USER_UNAUTHORIZED, HttpStatus.UNAUTHORIZED);
        }
        Long userId = claims.get("user_id", Long.class);
        User user = userService.findById(userId);

        UserContext userContext = new UserContext(
                user.getId(),
                user.getUsername(),
                user.getPhone(),
                user.getAvatar(),
                1,
                user.getTotalQuota(),
                user.getUsedQuota()
        );
        BeanUtils.copyProperties(user, userContext);
        log.info("登录用户 user:[{}]", userContext);
        // 用户信息传递：
        // 方式一：Request Attribute 传递
        // 方式二：ThreadLocal 传递
        UserContextThreadHolder.setUserContext(userContext);
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // 记得释放资源，避免内存泄露
        UserContextThreadHolder.clear();
        HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
    }
}
