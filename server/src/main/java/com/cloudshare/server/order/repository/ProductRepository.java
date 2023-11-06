package com.cloudshare.server.order.repository;

import com.cloudshare.server.order.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author novo
 * @since 2023/11/6
 */
public interface ProductRepository extends JpaRepository<Product, Long> {
}
