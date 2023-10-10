package com.cloudshare.server.controller;

import com.cloudshare.web.response.Response;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author novo
 * @since 2023/10/4
 */
@Controller
public class HelloWorldController {

    @GetMapping
    @ResponseBody
    public Response<String> hello() {
        return Response.success("hello world!");
    }
}