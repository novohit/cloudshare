package com.cloudshare.server.order.repository;

import com.cloudshare.server.order.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author novo
 * @since 2023/11/7
 */
public interface OrderRepository extends JpaRepository<Order, Long> {
}
