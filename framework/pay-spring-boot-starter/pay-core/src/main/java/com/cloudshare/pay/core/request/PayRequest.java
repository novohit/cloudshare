package com.cloudshare.pay.core.request;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class PayRequest {

    /**
     * 订单流水号
     */
    private String orderOutTradeNo;

    /**
     * 订单实际⽀付价格
     */
    private BigDecimal actualPayAmount;

    /**
     * 支付类型，微信-银行卡-支付宝
     */
    private String payType;

    private String title;

    private String description;

    /**
     * 业务参数 json格式
     */
    private String bizContent;

    /**
     * 订单超时时间 s
     */
    private Long timeOut;
}
