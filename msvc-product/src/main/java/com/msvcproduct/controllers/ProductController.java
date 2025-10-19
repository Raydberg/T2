package com.msvcproduct.controllers;

import com.msvcproduct.DTOs.ProductResponseDto;
import com.msvcproduct.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
@Slf4j
public class ProductController {

    private final ProductService productService;


    @GetMapping
    public ResponseEntity<ProductResponseDto> getAllProducts() {
        ProductResponseDto response = productService.getAllProducts();
        return ResponseEntity.ok(response);
    }


    @GetMapping("/with-delay")
    public ResponseEntity<ProductResponseDto> getAllProductsWithDelay() {
        ProductResponseDto response = productService.getAllProductsWithDelay();
        return ResponseEntity.ok(response);
    }



}
