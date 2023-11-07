package com.cloudshare.server.common.delay;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.cloudshare.server.common.constant.BizConstant;
import com.cloudshare.server.order.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author novo
 * @since 2023/11/7
 */
@Component
@Slf4j
public class DelayMsgTask {

    private final DelayingQueue delayingQueue;

    private final OrderService orderService;

    public DelayMsgTask(DelayingQueue delayingQueue, OrderService orderService) {
        this.delayingQueue = delayingQueue;
        this.orderService = orderService;
    }


    @Scheduled(cron = "*/30 * * * * ?")
    public void checkMessage() {
        List<DelayingMsg> messageList = delayingQueue.poll(BizConstant.ORDER_DELAY_QUEUE);
        for (DelayingMsg message : messageList) {
            String body = message.getBody();
            JSONObject jsonObject = JSON.parseObject(body);
            Long userId = jsonObject.getLong("userId");
            Long orderId = jsonObject.getLong("orderId");
            orderService.cancel(orderId, userId);
            // TODO 保证幂等性 可能会重复消费
            delayingQueue.remove(BizConstant.ORDER_DELAY_QUEUE, message);
        }
    }
}
