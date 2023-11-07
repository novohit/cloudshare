package com.cloudshare.server.order.service.impl;

import cn.hutool.core.util.RandomUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.cloudshare.common.util.SnowflakeUtil;
import com.cloudshare.pay.core.PayStrategyFactory;
import com.cloudshare.pay.core.PayType;
import com.cloudshare.pay.core.request.PayRequest;
import com.cloudshare.server.auth.UserContext;
import com.cloudshare.server.auth.UserContextThreadHolder;
import com.cloudshare.server.common.constant.BizConstant;
import com.cloudshare.server.common.delay.DelayingMsg;
import com.cloudshare.server.common.delay.DelayingQueue;
import com.cloudshare.server.order.controller.request.PlaceOrderReqDTO;
import com.cloudshare.server.order.enums.PayStateEnum;
import com.cloudshare.server.order.model.Order;
import com.cloudshare.server.order.model.Product;
import com.cloudshare.server.order.repository.OrderRepository;
import com.cloudshare.server.order.service.OrderService;
import com.cloudshare.server.order.service.ProductService;
import com.cloudshare.server.user.enums.PlanLevel;
import com.cloudshare.web.exception.BizException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @author novo
 * @since 2023/11/6
 */
@Service
@Slf4j
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;

    private final ProductService productService;

    private final PayStrategyFactory payStrategyFactory;

    private final DelayingQueue delayingQueue;

    public OrderServiceImpl(OrderRepository orderRepository, ProductService productService, PayStrategyFactory payStrategyFactory, DelayingQueue delayingQueue) {
        this.orderRepository = orderRepository;
        this.productService = productService;
        this.payStrategyFactory = payStrategyFactory;
        this.delayingQueue = delayingQueue;
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
        Long orderId = saveOrder2DB(orderOutTradeNo, reqDTO, product);
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
        // 发送关单延迟消息
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("userId", user.id());
        jsonObject.put("orderId", orderId);
        DelayingMsg message = new DelayingMsg(
                SnowflakeUtil.nextId(),
                jsonObject.toJSONString(),
                System.currentTimeMillis() + BizConstant.PLACE_ORDER_TIME_OUT,
                LocalDateTime.now()
        );
        delayingQueue.send(BizConstant.ORDER_DELAY_QUEUE, message);
        log.info("发起订单支付，订单号：{}，支付方式：{}，账号：{}，订单详情：{}，订单金额：{}",
                payRequest.getOrderOutTradeNo(),
                PayType.ALI_PAY_PC,
                payRequest.getAccountNo(),
                payRequest.getDescription(),
                payRequest.getActualPayAmount());
        return payStrategyFactory.chooseStrategy(reqDTO.payType().name())
                .pay(payRequest)
                .getBody();
    }

    @Override
    @Transactional
    public void cancel(Long orderId, Long userId) {
        int rows = orderRepository.cancel(orderId, userId, PayStateEnum.NEW, PayStateEnum.CANCEL);
        if (rows > 0) {
            log.info("未支付订单关闭 orderId:{} userId:{}", orderId, userId);
        }
    }

    @Override
    @Transactional
    public void changeOrderState(String outTradeNo, LocalDateTime payTime, Long userId) {
        int rows = orderRepository.changeState(outTradeNo, payTime, userId, PayStateEnum.NEW, PayStateEnum.PAID);
        if (rows > 0) {
            log.info("订单状态更新成功 NEW->PAID，订单号：{}", outTradeNo);
        } else {
            log.error("订单状态更新失败 NEW->PAID，订单号：{}", outTradeNo);
        }
    }


    private Long saveOrder2DB(String orderOutTradeNo, PlaceOrderReqDTO reqDTO, Product product) {
        UserContext user = UserContextThreadHolder.getUserContext();
        Long orderId = SnowflakeUtil.nextId();
        Order order = new Order();
        BeanUtils.copyProperties(reqDTO, order);
        order.setUserId(user.id());
        order.setOrderId(orderId);
        order.setUsername(user.username());
        order.setProductSnapshot(JSON.toJSONString(product));
        order.setOutTradeNo(orderOutTradeNo);
        order.setProductTitle(product.getTitle());
        order.setProductAmount(product.getAmount());
        order.setActualPayAmount(product.getAmount());
        order.setTotalAmount(reqDTO.actualPayAmount());
        order.setState(PayStateEnum.NEW);
        orderRepository.save(order);
        return orderId;
    }

    private void checkPrice(PlaceOrderReqDTO reqDTO, Product product) {
        BigDecimal serverTotal = BigDecimal.valueOf(reqDTO.buyNum()).multiply(product.getAmount());
        if (serverTotal.compareTo(reqDTO.actualPayAmount()) != 0) {
            throw new BizException("价格已经变动，请重新下单");
        }
    }
}
