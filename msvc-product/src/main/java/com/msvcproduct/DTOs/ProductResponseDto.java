package com.msvcproduct.DTOs;

import java.util.List;

public record ProductResponseDto(
        String message,
        List<Product> products,
        String source
) {}