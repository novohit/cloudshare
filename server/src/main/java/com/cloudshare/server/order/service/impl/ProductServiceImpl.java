package com.cloudshare.server.order.service.impl;

import com.cloudshare.server.order.model.Product;
import com.cloudshare.server.order.repository.ProductRepository;
import com.cloudshare.server.order.service.ProductService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author novo
 * @since 2023/11/6
 */
@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public List<Product> list() {
        return productRepository.findAll();
    }
}
