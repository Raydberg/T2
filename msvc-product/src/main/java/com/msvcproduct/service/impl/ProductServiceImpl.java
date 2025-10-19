package com.msvcproduct.service.impl;

import com.msvcproduct.DTOs.Product;
import com.msvcproduct.DTOs.ProductResponseDto;
import com.msvcproduct.exceptions.ProductServiceException;
import com.msvcproduct.service.ProductService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@Slf4j
public class ProductServiceImpl implements ProductService {

    private static final String CIRCUIT_BREAKER_NAME = "products";
    private final AtomicInteger attemptCounter = new AtomicInteger(0);

    @Override
    public ProductResponseDto getAllProducts() {
        log.info(" Obteniendo productos SIN delay");
        return new ProductResponseDto(
                "Productos recuperados exitosamente",
                getSimulatedProducts(),
                "NORMAL"
        );
    }

    @Override
    @TimeLimiter(name = CIRCUIT_BREAKER_NAME)
    @Retry(name = CIRCUIT_BREAKER_NAME)
    @CircuitBreaker(name = CIRCUIT_BREAKER_NAME, fallbackMethod = "fallbackGetProducts")
    public CompletableFuture<ProductResponseDto> getAllProductsWithDelay() {
        return CompletableFuture.supplyAsync(() -> {
            int currentAttempt = attemptCounter.incrementAndGet();
            log.info("INTENTO #{} de obtener productos con delay", currentAttempt);

            try {
                Thread.sleep(7000);

                attemptCounter.set(0);

                return new ProductResponseDto(
                        "Productos recuperados exitosamente después del retraso",
                        getSimulatedProducts(),
                        "DELAYED_SUCCESS"
                );

            } catch (
                    InterruptedException e) {
                Thread.currentThread().interrupt();
                log.error("INTENTO #{} FALLÓ - Thread interrumpido", currentAttempt);
                throw new ProductServiceException(
                        "Servicio interrumpido - Intento #" + currentAttempt,
                        e
                );
            } catch (
                    Exception e) {
                log.error("INTENTO #{} FALLÓ - Error: {}", currentAttempt, e.getMessage());
                throw new ProductServiceException(
                        "Error inesperado - Intento #" + currentAttempt,
                        e
                );
            }
        });
    }

    private CompletableFuture<ProductResponseDto> fallbackGetProducts(Exception ex) {
        int totalAttempts = attemptCounter.getAndSet(0);
        log.error("FALLBACK ACTIVADO después de {} intentos", totalAttempts);
        log.error("Razón de la falla: {}", ex.getMessage());

        ProductResponseDto fallbackResponse = new ProductResponseDto(
                String.format(
                        "Servicio no disponible después de %d intentos. " +
                                "Mostrando productos predeterminados. Razón: %s",
                        totalAttempts,
                        ex.getMessage()
                ),
                getFallbackProducts(),
                "FALLBACK"
        );

        return CompletableFuture.completedFuture(fallbackResponse);
    }


    private List<Product> getSimulatedProducts() {
        List<Product> products = new ArrayList<>();

        products.add(new Product(
                1L,
                " Laptop Dell XPS 15",
                "Portátil de alto rendimiento con 16GB RAM",
                BigDecimal.valueOf(1299.99),
                10
        ));

        products.add(new Product(
                2L,
                " iPhone 15 Pro",
                "Último smartphone Apple con chip A17",
                BigDecimal.valueOf(999.99),
                25
        ));


        return products;
    }


    private List<Product> getFallbackProducts() {
        List<Product> products = new ArrayList<>();

        products.add(new Product(
                999L,
                " PRODUCTO PREDETERMINADO 1",
                " Este es un producto en caché mostrado cuando el servicio no está disponible",
                BigDecimal.valueOf(0.00),
                0
        ));

        return products;
    }
}
