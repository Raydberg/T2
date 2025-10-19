package com.msvcproduct.service.impl;

import com.msvcproduct.DTOs.Product;
import com.msvcproduct.DTOs.ProductResponseDto;
import com.msvcproduct.exceptions.ProductServiceException;
import com.msvcproduct.service.ProductService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class ProductServiceImpl implements ProductService {

    private static final String CIRCUIT_BREAKER_NAME = "products";
    private int attemptCounter = 0;

    @Override
    public ProductResponseDto getAllProducts() {
        return new ProductResponseDto(
                "Productos recuperados exitosamente",
                getSimulatedProducts(),
                "NORMAL"
        );
    }

    @Override
    @CircuitBreaker(name = CIRCUIT_BREAKER_NAME, fallbackMethod = "fallbackGetProducts")
    @Retry(name = CIRCUIT_BREAKER_NAME)
    public ProductResponseDto getAllProductsWithDelay() {
        attemptCounter++;
        log.info(" Intento #{} para obtener productos con retraso", attemptCounter);

        try {
            log.warn(" Simulando un retraso en el servicio de 5 segundos...");
            TimeUnit.SECONDS.sleep(5);

            log.info(" Productos recuperados exitosamente después del retraso");
            attemptCounter = 0;

            return new ProductResponseDto(
                    "Productos recuperados exitosamente después del retraso",
                    getSimulatedProducts(),
                    "DELAYED_SUCCESS"
            );

        } catch (
                InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error(" El hilo fue interrumpido durante la simulación del retraso", e);
            throw new ProductServiceException(
                    "El servicio fue interrumpido mientras procesaba la solicitud",
                    e
            );
        } catch (
                Exception e) {
            log.error(" Ocurrió un error inesperado", e);
            throw new ProductServiceException(
                    "Error inesperado en el servicio de productos",
                    e
            );
        }
    }

    private ProductResponseDto fallbackGetProducts(Exception ex) {
        attemptCounter = 0;
        log.error(" SE ACTIVÓ EL MÉTODO ALTERNATIVO - El circuito está ABIERTO o los reintentos se agotaron");
        log.error(" Excepción: {}", ex.getMessage());

        return new ProductResponseDto(
                "El servicio está temporalmente no disponible. Mostrando productos en caché o predeterminados.",
                getFallbackProducts(),
                "FALLBACK"
        );
    }


    private List<Product> getSimulatedProducts() {
        List<Product> products = new ArrayList<>();

        products.add(new Product(
                1L,
                "Laptop Dell XPS 15",
                "Portátil de alto rendimiento con 16GB de RAM",
                BigDecimal.valueOf(1299.99),
                10
        ));

        products.add(new Product(
                2L,
                "iPhone 15 Pro",
                "Último smartphone de Apple con chip A17",
                BigDecimal.valueOf(999.99),
                25
        ));

        return products;
    }


    private List<Product> getFallbackProducts() {
        List<Product> products = new ArrayList<>();

        products.add(new Product(
                999L,
                "Producto Predeterminado 1",
                "Este es un producto en caché/predeterminado mostrado cuando el servicio no está disponible",
                BigDecimal.valueOf(0.00),
                0
        ));

        return products;
    }

}
