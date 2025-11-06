package org.example.simulacionpedidos.aspects;

import org.example.simulacionpedidos.orders.order;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.example.simulacionpedidos.service.SimulationState;

@Aspect
@Component
public class OrderAuditPerformanceAspect {

    private static final Logger log = LoggerFactory.getLogger(OrderAuditPerformanceAspect.class);
    private final SimulationState state; // Inyección de dependencia

    public OrderAuditPerformanceAspect(SimulationState state) {
        this.state = state; // Se inyecta el almacenamiento central
    }

    @Pointcut("@annotation(org.example.simulacionpedidos.annotations.Auditable) && args(order)")
    public void auditableOrderOperation(order order) {}

    @Around("auditableOrderOperation(order)")
    public Object measureExecutionTime(ProceedingJoinPoint joinPoint, order order) throws Throwable {
        long startTime = System.currentTimeMillis();

        // --- Auditoría de INICIO: Se registra en el estado ---
        String startMsg = "--- Auditoría: Inicio de proceso para " + order.toString() + " ---";
        state.addLog(startMsg); // Almacena el log
        log.info(startMsg); // Mantener el log de consola también para debug

        Object result = null;
        try {
            result = joinPoint.proceed();
        } finally {
            long duration = System.currentTimeMillis() - startTime;

            // --- Control de RENDIMIENTO: Se registra en el estado ---
            String perfMsg = "[PERFORMANCE] " + order.toString() + " procesado en " + duration + " ms";
            state.addLog(perfMsg); // Almacena el log
            log.info(perfMsg); // Mantener el log de consola
        }
        return result;
    }

    @AfterReturning("auditableOrderOperation(order)")
    public void logSuccessfulCompletion(order order) {
        // --- Auditoría de FIN (Éxito): Se registra en el estado ---
        String endMsg = "--- Auditoría: Fin de proceso para " + order.toString() + " ---";
        state.addLog(endMsg); // Almacena el log
        log.info(endMsg);
    }


    @AfterThrowing(pointcut = "auditableOrderOperation(order)", throwing = "ex")
    public void handleException(order order, Throwable ex) {
        // --- Manejo de EXCEPCIONES: Se registra en el estado ---
        String errorMsg = "[ERROR] " + order.toString() + " falló: " + ex.getMessage();
        state.addLog(errorMsg); // Almacena el log
        log.error(errorMsg);
    }
}