package org.example.simulacionpedidos.service;

import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Componente que almacena los logs y resultados de la simulación de forma segura.
 */
@Component
public class SimulationState {

    // Lista concurrente para que múltiples hilos escriban sin problemas.
    private final List<String> logs = new CopyOnWriteArrayList<>();
    
    // Almacena el tiempo total de la simulación.
    private long totalSimulationTime = 0;
    
    public void addLog(String message) {
        this.logs.add(message);
    }
    
    public void clearLogs() {
        this.logs.clear();
        this.totalSimulationTime = 0;
    }

    // Devuelve una lista no modificable para el acceso del controlador
    public List<String> getLogs() {
        return Collections.unmodifiableList(logs);
    }

    public long getTotalSimulationTime() {
        return totalSimulationTime;
    }

    public void setTotalSimulationTime(long totalSimulationTime) {
        this.totalSimulationTime = totalSimulationTime;
    }
}