package com.cloudshare.server.auth.interceptor;

import com.cloudshare.common.util.TokenUtil;
import com.cloudshare.server.auth.ShareContextThreadHolder;
import com.cloudshare.server.common.annotation.ShareTokenRequired;
import com.cloudshare.server.model.Share;
import com.cloudshare.server.service.ShareService;
import com.cloudshare.server.common.exception.BadRequestException;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

/**
 * @author novo
 * @since 2023/10/30
 */
@Slf4j
public class ShareInterceptor implements HandlerInterceptor {

    private static final String SHARE_TOKEN_HEADER = "SHARE-TOKEN";

    private final ShareService shareService;

    public ShareInterceptor(ShareService shareService) {
        this.shareService = shareService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        ShareTokenRequired shareTokenRequired = this.getAnnotation(handler);
        if (shareTokenRequired == null) {
            return true;
        }
        String shareToken = request.getHeader(SHARE_TOKEN_HEADER);
        if (!StringUtils.hasText(shareToken)) {
            shareToken = request.getParameter(SHARE_TOKEN_HEADER);
            if (!StringUtils.hasText(shareToken)) {
                throw new BadRequestException("Share-Token不存在");
            }
        }
        Claims claims = TokenUtil.verifyToken(shareToken);
        if (claims == null) {
            log.info("token不合法");
            throw new BadRequestException("Share-Token不存在");
        }
        Long shareId = claims.get("user_id", Long.class);
        Share share = shareService.detail(shareId);
        ShareContextThreadHolder.setShare(share);
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        ShareContextThreadHolder.clear();
        HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
    }

    private ShareTokenRequired getAnnotation(Object handler) {
        ShareTokenRequired annotation = null;
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            Method method = handlerMethod.getMethod();
            annotation = method.getAnnotation(ShareTokenRequired.class);
        }
        return annotation;
    }
}
