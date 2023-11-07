package com.cloudshare.pay.core;

import com.cloudshare.pay.core.response.PayCallBackResponse;

import java.util.Map;

@FunctionalInterface
public interface PayCallbackHandler {

    PayCallBackResponse handle(Map<String, String> paramMap);
}
