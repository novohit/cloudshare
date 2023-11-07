package com.cloudshare.server.order.service;

import com.cloudshare.server.order.controller.request.PlaceOrderReqDTO;

/**
 * @author novo
 * @since 2023/11/6
 */
public interface OrderService {
    String placeOrder(PlaceOrderReqDTO reqDTO);
}
