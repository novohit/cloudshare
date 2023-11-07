package com.cloudshare.pay.core.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public final class PayResponse {

    /**
     * 调用支付返回信息
     */
    private String body;
}
