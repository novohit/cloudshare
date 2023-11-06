package com.cloudshare.server.order.model;

import com.cloudshare.server.common.BaseModel;
import com.cloudshare.server.order.enums.PayStateEnum;
import com.cloudshare.server.order.enums.PayType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.Comment;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @author novo
 * @since 2023-09-19
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity(name = "`order`")
public class Order extends BaseModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long orderId;

    @Comment("商品标题")
    private String productTitle;

    @Comment("商品单价")
    private BigDecimal productAmount;

    @Comment("商品快照")
    private String productSnapshot;

    @Comment("购买数量")
    private Integer buyNum;

    @Comment("订单唯⼀标识")
    private String outTradeNo;

    @Comment("NEW未支付订单,PAY已经支付订单,CANCEL超时取消订单")
    private PayStateEnum state;

    @Comment("订单完成时间")
    private LocalDateTime payTime;

    @Comment("订单总⾦额")
    private BigDecimal totalAmount;

    @Comment("订单实际⽀付价格")
    private BigDecimal actualPayAmount;

    @Comment("支付类型，微信-银行卡-支付宝")
    private PayType payType;

    @Comment("账户名")
    private String username;

    private Long accountNo;

    /**
     * 发票类型：0->不开发票；1->电子发票；2->纸质发票
     */
    private String billType;

    /**
     * 发票抬头
     */
    private String billHeader;

    /**
     * 发票内容
     */
    private String billContent;

    /**
     * 发票收票人电话
     */
    private String billReceiverPhone;

    /**
     * 发票收票人邮箱
     */
    private String billReceiverEmail;
}
