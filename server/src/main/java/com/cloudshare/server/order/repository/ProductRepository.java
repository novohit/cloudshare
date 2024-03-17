package com.cloudshare.server.order.repository;

import com.cloudshare.server.order.model.Product;
import com.cloudshare.server.enums.PlanLevel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * @author novo
 * @since 2023/11/6
 */
public interface ProductRepository extends JpaRepository<Product, Long> {

    Optional<Product> findByPlanAndDeletedAtIsNull(PlanLevel planLevel);
}
