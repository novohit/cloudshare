package com.cloudshare.server.order.controller.request;

import com.cloudshare.server.order.enums.PayType;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

public record PlaceOrderReqDTO(
        @NotNull Long productId,
        @NotNull Integer buyNum,
        @NotNull BigDecimal actualPayAmount,
        @NotNull PayType payType
) {
}
