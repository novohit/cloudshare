package com.cloudshare.server.order.controller;

import com.cloudshare.pay.core.PayStrategyFactory;
import com.cloudshare.pay.core.PayType;
import com.cloudshare.pay.core.response.PayCallBackResponse;
import com.cloudshare.server.order.service.impl.PayCallBackHandlerImpl;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Controller
@RequestMapping("/api/pay/callback")
public class PayCallbackController {

    private final PayStrategyFactory payStrategyFactory;

    private final PayCallBackHandlerImpl payCallBackHandler;

    public PayCallbackController(PayStrategyFactory payStrategyFactory, PayCallBackHandlerImpl payCallBackHandler) {
        this.payStrategyFactory = payStrategyFactory;
        this.payCallBackHandler = payCallBackHandler;
    }

    @PostMapping
    @ResponseBody
    public String callback(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PayCallBackResponse payCallBackResponse = payStrategyFactory.chooseStrategy(PayType.ALI_PAY_PC)
                .callback(request, payCallBackHandler);
        return payCallBackResponse.getBody();
    }
}
