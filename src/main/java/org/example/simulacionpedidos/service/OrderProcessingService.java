package org.example.simulacionpedidos.service;


import org.example.simulacionpedidos.annotations.Auditable;
import org.example.simulacionpedidos.orders.order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class OrderProcessingService {

    private static final Logger log = LoggerFactory.getLogger(OrderProcessingService.class);

    // Fuente de aleatoriedad para simular fallos
    private static final Random random = new Random();

    /**
     * Simula el procesamiento de un pedido.
     * * - @Async: Se ejecuta en un hilo separado del ThreadPoolTaskExecutor.
     * - @Auditable: Será interceptado por el Aspecto para auditoría y medición.
     */
    @Async
    @Auditable
    public void processOrder(order order) {
        log.info("[INFO] Iniciando lógica de negocio para: {}", order.toString());

        // --- 1. Simular Tareas y Pausas ---

        // Simular tiempo de espera aleatorio (1 a 3.5 segundos)
        int sleepTime = ThreadLocalRandom.current().nextInt(1000, 3500);
        log.info("[DEBUG] {} simulando trabajo por {} ms...", order.toString(), sleepTime);
        try {
            Thread.sleep(sleepTime);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            // Esto solo ocurriría si el hilo es interrumpido externamente
            log.warn("El procesamiento del {} fue interrumpido.", order.toString());
        }

        // --- 2. Simular Fallo Aleatorio ---

        // Simular fallo aleatorio (aprox. 30% de las veces)
        if (random.nextDouble() < 0.3) {
            log.error("[DEBUG] {} simulará un fallo...", order.toString());
            // Lanzar una excepción. El aspecto @AfterThrowing la capturará.
            if (order.getId() % 2 == 0) {
                throw new RuntimeException("Pago rechazado (Error simulado)");
            } else {
                throw new RuntimeException("Error al verificar stock (Error simulado)");
            }
        }

        // --- 3. Fin de la Lógica de Negocio (Exitoso) ---

        // Si el código llega aquí, el Aspecto @AfterReturning registrará el éxito.
        log.info("[INFO] Lógica de negocio completada para: {}", order.toString());
    }
}