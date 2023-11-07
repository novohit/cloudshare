package com.cloudshare.pay.core;

import com.cloudshare.pay.core.request.PayRequest;
import com.cloudshare.pay.core.response.PayCallBackResponse;
import com.cloudshare.pay.core.response.PayResponse;

import javax.servlet.http.HttpServletRequest;

public interface PayStrategy {

    /**
     * 执行策略标识
     */
    default String mark() {
        return null;
    }

    PayResponse pay(PayRequest payRequest);

    PayResponse cancel(PayRequest payRequest);

    PayResponse refund(PayRequest payRequest);

    PayCallBackResponse callback(HttpServletRequest request, PayCallbackHandler callbackHandler);

}
