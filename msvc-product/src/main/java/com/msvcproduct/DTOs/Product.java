package com.msvcproduct.DTOs;

import java.math.BigDecimal;

public record Product(
        Long id,
        String name,
        String description,
        BigDecimal price,
        Integer stock
) {
}