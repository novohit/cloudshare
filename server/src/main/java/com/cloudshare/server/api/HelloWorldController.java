package com.cloudshare.server.api;

import com.cloudshare.web.response.Response;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author novo
 * @since 2023/10/4
 */
@RestController
public class HelloWorldController {

    @GetMapping
    public Response<String> hello() {
        return Response.success("hello world!");
    }
}
