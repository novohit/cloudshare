package com.cloudshare.pay.ali;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = AliPayProperties.PREFIX)
@Data
public class AliPayProperties {

    public static final String PREFIX = "pay.alipay";

    /**
     * 开放平台上创建的应用的 ID
     */
    private String appId;

    /**
     * 商户私钥
     */
    private String privateKey;

    /**
     * 支付宝公钥字符串（公钥模式下设置，证书模式下无需设置）
     */
    private String alipayPublicKey;

    /**
     * 网关地址
     * 线上：https://openapi.alipay.com/gateway.do
     * 沙箱：https://openapi.alipaydev.com/gateway.do
     */
    private String serverUrl;

    /**
     * 支付结果回调地址
     */
    private String notifyUrl;

    /**
     * 支付成功跳转地址（非必填）
     */
    private String returnUrl;

    /**
     * 报文格式，推荐：json
     */
    private String format;

    /**
     * 字符串编码，推荐：utf-8
     */
    private String charset;

    /**
     * 签名算法类型，推荐：RSA2
     */
    private String signType;
}
