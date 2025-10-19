package com.msvcproduct.service;

import com.msvcproduct.DTOs.ProductResponseDto;

public interface ProductService {
    ProductResponseDto getAllProducts();
    ProductResponseDto getAllProductsWithDelay();
}