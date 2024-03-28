package com.cloudshare.server.order.controller;

import com.cloudshare.server.order.model.Product;
import com.cloudshare.server.order.service.ProductService;
import com.cloudshare.server.common.response.Response;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author novo
 * @since 2023/11/6
 */
@RestController
@RequestMapping("/product")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/list")
    public Response<List<Product>> list() {
        List<Product> resp = productService.list();
        return Response.success(resp);
    }
}
