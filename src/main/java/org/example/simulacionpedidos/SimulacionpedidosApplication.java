package org.example.simulacionpedidos;

import org.example.simulacionpedidos.orders.order;
import org.example.simulacionpedidos.service.OrderProcessingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@SpringBootApplication
@EnableAsync // (2) Habilita la ejecución de métodos @Async
public class SimulacionpedidosApplication implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(SimulacionpedidosApplication.class);

    // Inyección de dependencia del servicio de procesamiento
    private final OrderProcessingService orderProcessingService;

    public SimulacionpedidosApplication(OrderProcessingService orderProcessingService) {
        this.orderProcessingService = orderProcessingService;
    }

    public static void main(String[] args) {
        SpringApplication.run(SimulacionpedidosApplication.class, args);
    }

    /**
     * (3) Configuración del ExecutorService para los hilos de @Async.
     * Define el pool de hilos que ejecutará concurrentemente los pedidos.
     */
    @Bean(name = "taskExecutor")
    public TaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(10); // 10 hilos para procesar los 10 pedidos simultáneos
        executor.setMaxPoolSize(20);
        executor.setQueueCapacity(500);
        executor.setThreadNamePrefix("Order-Thread-");
        executor.initialize();
        return executor;
    }

    /**
     * (4) Lógica de simulación: se ejecuta inmediatamente después del arranque de Spring.
     */
    @Override
    public void run(String... args) throws Exception {
        log.info("\n\n=== INICIO DE SIMULACIÓN DE PEDIDOS ===\n");

        List<order> orders = createOrders(10);
        long startTime = System.currentTimeMillis();

        // 1. Enviar pedidos a procesar concurrentemente
        for (order order : orders) {
            log.info("[INFO] Pedido {} recibido para el cliente: {}", order.getId(), order.getCustomerName());
            // Llamar al método @Async, que será interceptado por el Aspecto
            orderProcessingService.processOrder(order);
        }

        // 2. Esperar a que todos los hilos terminen.
        // Se hace una pausa forzada para asegurar que todos los hilos terminen
        // antes de imprimir el resumen final.
        log.info("\n[INFO] Esperando la finalización de todos los procesos. Esto puede tardar hasta 5 segundos...");
        TimeUnit.SECONDS.sleep(5);

        long endTime = System.currentTimeMillis();
        long totalDuration = endTime - startTime;

        log.info("\n=== PROCESAMIENTO FINALIZADO ===");
        log.info("Tiempo total de simulación: {} ms (Aprox.)", totalDuration);
        log.info("(Nota: Los contadores de éxito/error se registran en el Aspecto, pero no se consolidan en este resumen por simplicidad.)");
    }

    /**
     * Método auxiliar para crear 10 pedidos de ejemplo.
     */
    private List<order> createOrders(int count) {
        List<order> orders = new ArrayList<>();
        // Nombres de ejemplo para cumplir con el output esperado
        String[] names = {"Ana López", "Carlos Gómez", "Marta Ruiz", "Diego Torres", "Laura Fernández",
                "Pedro Ramírez", "Sofía Medina", "Juan Pérez", "Lucía Vargas", "Jorge Castillo"};

        for (int i = 0; i < count; i++) {
            orders.add(new order((long) i + 1, 100.0 * (i + 1), names[i % names.length]));
        }
        return orders;
    }
}