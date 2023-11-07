package com.cloudshare.server.order.controller;

import com.cloudshare.server.order.controller.request.PlaceOrderReqDTO;
import com.cloudshare.server.order.service.OrderService;
import com.cloudshare.web.response.Response;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author novo
 * @since 2023/11/6
 */
@RestController
@RequestMapping("/order")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }


    @PostMapping(path = "/place")
    public Response<String> placeOrder(@RequestBody @Validated PlaceOrderReqDTO request) {
        return Response.success(orderService.placeOrder(request));
    }
}
