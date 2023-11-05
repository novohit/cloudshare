package com.cloudshare.server.recycle.controller;

import com.cloudshare.web.exception.BizException;
import com.cloudshare.web.response.Response;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author novo
 * @since 2023/11/5
 */
@RestController
@RequestMapping("recycle")
public class RecycleController {

    @GetMapping
    public Response<Void> list() {
        throw new BizException("敬请期待！");
    }
}
