package com.cloudshare.server.link.controller;

import com.cloudshare.server.common.util.CheckUtil;
import com.cloudshare.server.link.model.ShortLink;
import com.cloudshare.server.link.service.ShortLinkService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 短链解析接口
 *
 * @author novo
 * @since 2023-03-16
 */
@Controller
@RequestMapping("/s")
@Slf4j
public class ShortLinkController {

    private final ShortLinkService shortLinkService;

    @Value("${cloudshare.share.front-end.url}")
    private String shareFrontendUrl;

    public ShortLinkController(ShortLinkService shortLinkService) {
        this.shortLinkService = shortLinkService;
    }

    /**
     * 短链码跳转
     *
     * @param code
     * @param response
     */
    @GetMapping("/{code}")
    public void dispatch(@PathVariable("code") String code, HttpServletRequest request, HttpServletResponse response) {
        log.info("code:[{}]", code);
        /**
         * 什么要用 302 跳转而不是 301
         * 301 是永久重定向，302 是临时重定向。
         * 短地址一经生成就不会变化，所以用 301 是同时对服务器压力也会有一定减少
         * 但是如果使用了 301，无法统计到短地址被点击的次数。
         * 所以选择302虽然会增加服务器压力，但是有很多数据可以获取进行分析
         */
        // 判断短链码是否合法
        if (CheckUtil.isLetterOrDigit(code)) {
            ShortLink shortLink = shortLinkService.findOneByCode(code);
            if (shortLink != null) {
                shortLinkService.incrementPV(code);
                response.setHeader("Location", shortLink.getOriginalUrl());
                // 302跳转
                response.setStatus(HttpStatus.FOUND.value());
            } else {
                response.setHeader("Location", shareFrontendUrl);
                response.setStatus(HttpStatus.FOUND.value());
            }
        } else {
            response.setHeader("Location", shareFrontendUrl);
            response.setStatus(HttpStatus.FOUND.value());
        }
    }
}
