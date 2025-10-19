package com.msvcproduct.service;

import com.msvcproduct.DTOs.ProductResponseDto;

import java.util.concurrent.CompletableFuture;

public interface ProductService {
    ProductResponseDto getAllProducts();

    CompletableFuture<ProductResponseDto> getAllProductsWithDelay();
}
