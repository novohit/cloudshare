package com.cloudshare.server.order.service.impl;

import cn.hutool.core.util.RandomUtil;
import com.alibaba.fastjson.JSON;
import com.cloudshare.common.util.SnowflakeUtil;
import com.cloudshare.pay.core.PayStrategyFactory;
import com.cloudshare.pay.core.request.PayRequest;
import com.cloudshare.server.auth.UserContext;
import com.cloudshare.server.auth.UserContextThreadHolder;
import com.cloudshare.server.common.constant.BizConstant;
import com.cloudshare.server.order.controller.request.PlaceOrderReqDTO;
import com.cloudshare.server.order.enums.PayStateEnum;
import com.cloudshare.server.order.model.Order;
import com.cloudshare.server.order.model.Product;
import com.cloudshare.server.order.repository.OrderRepository;
import com.cloudshare.server.order.service.OrderService;
import com.cloudshare.server.order.service.ProductService;
import com.cloudshare.server.user.enums.PlanLevel;
import com.cloudshare.web.exception.BizException;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

/**
 * @author novo
 * @since 2023/11/6
 */
@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;

    private final ProductService productService;

    private final PayStrategyFactory payStrategyFactory;

    public OrderServiceImpl(OrderRepository orderRepository, ProductService productService, PayStrategyFactory payStrategyFactory) {
        this.orderRepository = orderRepository;
        this.productService = productService;
        this.payStrategyFactory = payStrategyFactory;
    }


    /**
     * 1. TODO 防止表单重复提交
     * 2. 计算价格
     * 3. 订单入库
     * 4. 发送延迟消息
     *
     * @param reqDTO
     * @return
     */
    @Override
    @Transactional
    public String placeOrder(PlaceOrderReqDTO reqDTO) {
        UserContext user = UserContextThreadHolder.getUserContext();
        if (user.plan().equals(PlanLevel.PLUS)) {
            throw new BizException("您已经是会员了");
        }
        Product product = productService.detail(reqDTO.productId());
        // 计算价格
        checkPrice(reqDTO, product);
        // 订单入库
        String orderOutTradeNo = RandomUtil.randomString(32);
        saveOrder2DB(orderOutTradeNo, reqDTO, product);
        // 调起支付
        PayRequest payRequest = PayRequest.builder()
                .orderOutTradeNo(orderOutTradeNo)
                .accountNo(user.id())
                .actualPayAmount(reqDTO.actualPayAmount())
                .payType(reqDTO.payType().name())
                .title(product.getTitle())
                .description(product.getDetail())
                .timeOut(BizConstant.PLACE_ORDER_TIME_OUT)
                .build();
        // TODO 发送延迟消息
        return payStrategyFactory.chooseStrategy(reqDTO.payType().name())
                .pay(payRequest)
                .getBody();
    }


    private void saveOrder2DB(String orderOutTradeNo, PlaceOrderReqDTO reqDTO, Product product) {
        UserContext user = UserContextThreadHolder.getUserContext();
        Order order = new Order();
        BeanUtils.copyProperties(reqDTO, order);
        order.setUserId(user.id());
        order.setOrderId(SnowflakeUtil.nextId());
        order.setUsername(user.username());
        order.setProductSnapshot(JSON.toJSONString(product));
        order.setOutTradeNo(orderOutTradeNo);
        order.setProductTitle(product.getTitle());
        order.setProductAmount(product.getAmount());
        order.setActualPayAmount(product.getAmount());
        order.setTotalAmount(reqDTO.actualPayAmount());
        order.setState(PayStateEnum.NEW);
        orderRepository.save(order);
    }

    private void checkPrice(PlaceOrderReqDTO reqDTO, Product product) {
        BigDecimal serverTotal = BigDecimal.valueOf(reqDTO.buyNum()).multiply(product.getAmount());
        if (serverTotal.compareTo(reqDTO.actualPayAmount()) != 0) {
            throw new BizException("价格已经变动，请重新下单");
        }
    }
}
