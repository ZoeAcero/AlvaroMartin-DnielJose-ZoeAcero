package org.example.simulacionpedidos.service;

import org.example.simulacionpedidos.annotations.Auditable;
import org.example.simulacionpedidos.orders.order;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Prueba de integración: invoca el método @Auditable del servicio real
 * y verifica que el aspecto registre logs en SimulationState.
 */
@SpringBootTest
class OrderProcessingServiceAspectIntegrationTest {

    @Autowired
    private OrderProcessingService service;

    @Autowired
    private SimulationState state;

    @BeforeEach
    void clearState() {
        state.clear();
    }

    @Test
    void auditableMethodProducesAuditOrErrorLogs() throws Exception {
        // Busca un método @Auditable(order) en el servicio
        Method target = null;
        for (Method m : OrderProcessingService.class.getDeclaredMethods()) {
            if (m.isAnnotationPresent(Auditable.class)) {
                Class<?>[] params = m.getParameterTypes();
                if (params.length == 1 && params[0].equals(order.class)) {
                    target = m;
                    break;
                }
            }
        }
        assertNotNull(target, "No se encontró un método @Auditable(order) en OrderProcessingService");

        // Invoca el método con un pedido concreto
        order sample = new order(1L, 199.99, "Cliente Test");
        try {
            target.setAccessible(true);
            target.invoke(service, sample);
            // Espera hasta 5 segundos o hasta que se registre el log final
            for (int i = 0; i < 50; i++) {
                boolean hasEndOrError = state.getLogs().stream()
                        .anyMatch(l -> l.startsWith("--- Auditoría: Fin") || l.startsWith("[ERROR]"));
                if (hasEndOrError) break;
                Thread.sleep(100); // comprueba cada 100 ms
            }
        } catch (Exception ex) {
            // Excepción esperada en algunos pedidos simulados
        }


        // Valida que el aspecto haya dejado traza en el estado
        List<String> logs = state.getLogs();
        assertFalse(logs.isEmpty(), "Debe haberse registrado alguna traza en SimulationState");

        boolean hasAuditStart = logs.stream().anyMatch(l -> l.startsWith("--- Auditoría: Inicio"));
        boolean hasAuditEndOrError = logs.stream().anyMatch(l -> l.startsWith("--- Auditoría: Fin") || l.startsWith("[ERROR]"));
        assertTrue(hasAuditStart, "Debe existir log de inicio de auditoría");
        assertTrue(hasAuditEndOrError, "Debe existir log de fin o de error");
    }
}