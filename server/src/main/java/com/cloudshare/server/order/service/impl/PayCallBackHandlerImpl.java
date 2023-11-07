package com.cloudshare.server.order.service.impl;

import com.cloudshare.pay.core.PayCallbackHandler;
import com.cloudshare.pay.core.response.PayCallBackResponse;
import com.cloudshare.server.common.constant.BizConstant;
import com.cloudshare.server.order.enums.PayType;
import com.cloudshare.server.order.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class PayCallBackHandlerImpl implements PayCallbackHandler {

    private final OrderService orderService;

    private final StringRedisTemplate stringRedisTemplate;


    public PayCallBackHandlerImpl(OrderService orderService, StringRedisTemplate stringRedisTemplate) {
        this.orderService = orderService;
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @Override
    @Transactional
    public PayCallBackResponse handle(Map<String, String> paramMap) {
        String outTradeNo = paramMap.get("out_trade_no");
        String totalAmount = paramMap.get("total_amount");
        String payTimeStr = paramMap.get("gmt_payment");
        LocalDateTime payTime;
        try {
            payTime = LocalDateTime.parse(payTimeStr, DateTimeFormatter.ofPattern(BizConstant.DATETIME_PATTERN));
        } catch (Exception e) {
            // try-catch 兜底
            payTime = LocalDateTime.now();
            log.error("支付宝日期解析异常", e);
        }
        String subject = paramMap.get("subject");
        String body = paramMap.get("body");
        Long accountNo = Long.valueOf(paramMap.get("passback_params"));

        // 上游第三方支付回调消息不是幂等的，有可能发送多次消息，需要下游在业务或数据库上保证幂等性
        Boolean success = stringRedisTemplate.opsForValue()
                .setIfAbsent(BizConstant.ORDER_PAID_HANDLER_PREFIX + outTradeNo, "SUCCESS", 12, TimeUnit.HOURS);
        if (Boolean.TRUE.equals(success)) {
            log.info(
                    "订单支付成功回调，订单号：{}，支付方式：{}，订单详情：{}，订单金额：{}",
                    outTradeNo,
                    PayType.ALI_PAY_PC,
                    subject,
                    totalAmount
            );
            orderService.changeOrderState(outTradeNo, payTime, accountNo);
            return new PayCallBackResponse("success");
        } else {
            log.error(
                    "订单已处理，订单号：{}，支付方式：{}，订单详情：{}，订单金额：{}",
                    outTradeNo,
                    PayType.ALI_PAY_PC,
                    subject,
                    totalAmount
            );
            return new PayCallBackResponse("fail");
        }
    }
}
