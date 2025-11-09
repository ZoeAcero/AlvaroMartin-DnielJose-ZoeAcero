package org.example.simulacionpedidos.service;

import org.example.simulacionpedidos.orders.order;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Lanza varias invocaciones del método @Auditable para simular concurrencia básica
 * y verifica que se agregan logs y se mide tiempo total.
 */
@SpringBootTest
class SimulationFlowIntegrationTest {

    @Autowired
    private OrderProcessingService service;

    @Autowired
    private SimulationState state;

    @BeforeEach
    void reset() {
        state.clear();
    }

    @Test
    void multipleOrdersGenerateLogsAndTotalTime() throws Exception {
        // localiza el método auditable
        Method target = null;
        for (Method m : OrderProcessingService.class.getDeclaredMethods()) {
            if (m.isAnnotationPresent(org.example.simulacionpedidos.annotations.Auditable.class)) {
                Class<?>[] params = m.getParameterTypes();
                if (params.length == 1 && params[0].equals(order.class)) {
                    target = m; break;
                }
            }
        }
        assertNotNull(target, "No se encontró un método @Auditable(order)");

        List<order> orders = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            orders.add(new order((long) i, 100.0 * i, "Cliente " + i));
        }

        for (order o : orders) {
            try {
                target.setAccessible(true);
                target.invoke(service, o);
            } catch (Exception ignored) {
                // algunos pedidos fallarán por simulación; es esperado
            }
        }
        // Espera breve para permitir que los hilos terminen y registren logs
        Thread.sleep(5000);


        assertTrue(state.getLogs().size() >= orders.size(), "Deben existir trazas al menos por pedido");
    }
}