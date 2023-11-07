package com.cloudshare.pay.ali;

import com.alibaba.fastjson.JSON;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.AlipayConfig;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.AlipayTradePagePayModel;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.response.AlipayTradePagePayResponse;
import com.cloudshare.pay.core.PayCallbackHandler;
import com.cloudshare.pay.core.PayStrategy;
import com.cloudshare.pay.core.PayType;
import com.cloudshare.pay.core.request.PayRequest;
import com.cloudshare.pay.core.response.PayCallBackResponse;
import com.cloudshare.pay.core.response.PayResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class AliPayStrategy implements PayStrategy {

    private final AliPayProperties properties;

    public AliPayStrategy(AliPayProperties properties) {
        this.properties = properties;
    }

    @Override
    public String mark() {
        return PayType.ALI_PAY_PC;
    }

    @Override
    public PayResponse pay(PayRequest payRequest) {
        try {
            AlipayConfig alipayConfig = new AlipayConfig();
            BeanUtils.copyProperties(properties, alipayConfig);
            AlipayClient alipayClient = new DefaultAlipayClient(alipayConfig);
            AlipayTradePagePayRequest request = new AlipayTradePagePayRequest();
            // SDK已经封装掉了公共参数，这里只需要传入业务参数
            AlipayTradePagePayModel model = new AlipayTradePagePayModel();
            model.setSubject(payRequest.getTitle());
            model.setBody(payRequest.getDescription());
            model.setTimeoutExpress("30m");
            model.setOutTradeNo(payRequest.getOrderOutTradeNo());
            model.setTotalAmount(payRequest.getActualPayAmount().toString());
            model.setProductCode("FAST_INSTANT_TRADE_PAY");
            model.setPassbackParams(payRequest.getBizContent());
            request.setNotifyUrl(properties.getNotifyUrl());
            if (StringUtils.hasText(properties.getReturnUrl())) {
                request.setReturnUrl(properties.getReturnUrl());
            }
            request.setBizModel(model);
            AlipayTradePagePayResponse response = alipayClient.pageExecute(request);
            log.debug("发起订单支付，订单号：{}，支付方式：{}，订单详情：{}，订单金额：{}， 业务参数：{}，\n调用支付返回：\n\n{}\n",
                    payRequest.getOrderOutTradeNo(),
                    PayType.ALI_PAY_PC,
                    payRequest.getDescription(),
                    payRequest.getActualPayAmount(),
                    payRequest.getBizContent(),
                    JSON.toJSONString(response));
            if (!response.isSuccess()) {
                throw new RuntimeException("调用支付宝发起支付异常");
            }
            return new PayResponse(response.getBody());
        } catch (AlipayApiException ex) {
            throw new RuntimeException("调用支付宝支付异常");
        }
    }

    @Override
    public PayResponse cancel(PayRequest payRequest) {
        return null;
    }

    @Override
    public PayResponse refund(PayRequest payRequest) {
        return null;
    }

    @Override
    public PayCallBackResponse callback(HttpServletRequest request, PayCallbackHandler callbackHandler) {
        Map<String, String> paramMap = new HashMap<>();

        Enumeration<String> parameterNames = request.getParameterNames();
        while (parameterNames.hasMoreElements()) {
            String paramName = parameterNames.nextElement();
            String paramValue = request.getParameter(paramName);
            paramMap.put(paramName, paramValue);
        }
        try {
            boolean signVerified = AlipaySignature.rsaCheckV1(paramMap,
                    properties.getAlipayPublicKey(),
                    properties.getCharset(),
                    properties.getSignType());
            if (!signVerified) {
                throw new RuntimeException("支付回调验签失败");
            }
            return callbackHandler.handle(paramMap);
        } catch (AlipayApiException e) {
            e.printStackTrace();
            throw new RuntimeException("支付回调验签失败");
        }
    }
}
