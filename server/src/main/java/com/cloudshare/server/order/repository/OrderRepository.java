package com.cloudshare.server.order.repository;

import com.cloudshare.server.order.enums.PayStateEnum;
import com.cloudshare.server.order.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

/**
 * @author novo
 * @since 2023/11/7
 */
public interface OrderRepository extends JpaRepository<Order, Long> {

    @Modifying
    @Query("update product_order as o " +
            "set o.state = :cancel " +
            "where o.state = :newType " +
            "and o.userId = :userId " +
            "and o.orderId = :orderId ")
    int cancel(@Param("orderId") Long orderId,
               @Param("userId") Long userId,
               @Param("newType") PayStateEnum newType,
               @Param("cancel") PayStateEnum cancel);

    @Modifying
    @Query("update product_order as o " +
            "set o.state = :paid, " +
            "o.payTime = :payTime " +
            "where o.state = :newType " +
            "and o.userId = :userId " +
            "and o.outTradeNo = :outTradeNo ")
    int changeState(@Param("outTradeNo") String outTradeNo,
                    @Param("payTime") LocalDateTime payTime,
                    @Param("userId") Long userId,
                    @Param("newType") PayStateEnum newType,
                    @Param("paid") PayStateEnum paid);
}
