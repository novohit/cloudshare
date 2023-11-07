package com.cloudshare.server.order.service;

import com.cloudshare.server.order.model.Product;

import java.util.List;

/**
 * @author novo
 * @since 2023/11/6
 */
public interface ProductService {

    List<Product> list();

    Product detail(Long productId);

}
