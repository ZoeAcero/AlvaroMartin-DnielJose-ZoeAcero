package org.example.simulacionpedidos.controller;

import org.example.simulacionpedidos.orders.order;
import org.example.simulacionpedidos.service.OrderProcessingService;
import org.example.simulacionpedidos.service.SimulationState;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Controller
@RequestMapping("/")
public class SimulationController {

    private final OrderProcessingService orderProcessingService;
    private final SimulationState state;

    // Inyección de dependencias
    public SimulationController(OrderProcessingService orderProcessingService, SimulationState state) {
        this.orderProcessingService = orderProcessingService;
        this.state = state;
    }

    @GetMapping
    public String index(Model model) {
        model.addAttribute("hasRun", false);
        return "simulation";
    }

    @GetMapping("/simular")
    public String runSimulation(Model model) throws InterruptedException {
        state.clearLogs(); // Limpiar resultados anteriores
        
        List<order> orders = createOrders(10);
        long startTime = System.currentTimeMillis();

        // 1. Lanzar todos los pedidos (se ejecutan en hilos separados)
        for (order order : orders) {
            state.addLog("[INFO] Pedido " + order.getId() + " recibido para el cliente: " + order.getCustomerName());
            orderProcessingService.processOrder(order);
        }

        // 2. Esperar a que todos los hilos terminen (Asumo que el máx. es 5s)
        TimeUnit.SECONDS.sleep(5); 

        long totalDuration = System.currentTimeMillis() - startTime;
        state.setTotalSimulationTime(totalDuration);
        
        // 3. Pasar resultados a la plantilla
        model.addAttribute("hasRun", true);
        model.addAttribute("logs", state.getLogs());
        model.addAttribute("totalTime", state.getTotalSimulationTime());

        return "simulation"; // Devuelve el nombre de la plantilla
    }
    
    // Método auxiliar para crear pedidos
    private List<order> createOrders(int count) {
        List<order> orders = new ArrayList<>();
        String[] names = {"Ana López", "Carlos Gómez", "Marta Ruiz", "Diego Torres", "Laura Fernández",
                          "Pedro Ramírez", "Sofía Medina", "Juan Pérez", "Lucía Vargas", "Jorge Castillo"};
        
        for (int i = 0; i < count; i++) {
            orders.add(new order((long) i + 1, 100.0 * (i + 1), names[i % names.length]));
        }
        return orders;
    }
}