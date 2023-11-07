package com.cloudshare.server.order.service;

import com.cloudshare.server.order.controller.request.PlaceOrderReqDTO;

import java.time.LocalDateTime;

/**
 * @author novo
 * @since 2023/11/6
 */
public interface OrderService {

    String placeOrder(PlaceOrderReqDTO reqDTO);

    void cancel(Long orderId, Long userId);

    void changeOrderState(String outTradeNo, LocalDateTime payTime, Long userId);
}
