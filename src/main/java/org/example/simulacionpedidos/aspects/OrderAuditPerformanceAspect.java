package org.example.simulacionpedidos.aspects;

import org.example.simulacionpedidos.orders.order;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect // (1) Marca la clase como un Aspecto AOP
@Component // Componente de Spring para que sea detectado e inyectado
public class OrderAuditPerformanceAspect {

    private static final Logger log = LoggerFactory.getLogger(OrderAuditPerformanceAspect.class);

    /**
     * (2) Pointcut: Define dónde se aplicarán los Advice.
     * Intercepta cualquier método marcado con @Auditable que acepte un objeto Order.
     */
    @Pointcut("@annotation(org.example.simulacionpedidos.annotations.Auditable) && args(order)")
    public void auditableOrderOperation(order order) {
        // Este método está vacío; solo define el punto de corte.
    }

    /**
     * (3) Advice @Around: Control de Rendimiento y Registro de Inicio.
     * Este Advice envuelve la ejecución del método de negocio.
     */
    @Around("auditableOrderOperation(order)")
    public Object measureExecutionTime(ProceedingJoinPoint joinPoint, order order) throws Throwable {
        long startTime = System.currentTimeMillis();

        // --- Auditoría de INICIO ---
        log.info("--- Auditoría: Inicio de proceso para {} ---", order.toString());

        Object result = null;
        try {
            // Ejecuta el método original (la lógica de negocio)
            result = joinPoint.proceed();
        } finally {
            // Este bloque garantiza la medición del tiempo, incluso si ocurre una excepción
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;

            // --- Control de RENDIMIENTO ---
            log.info("[PERFORMANCE] {} procesado en {} ms", order.toString(), duration);
        }
        return result;
    }

    /**
     * (4) Advice @AfterReturning: Registro de Finalización Exitosa.
     * Se ejecuta SOLO si el método de negocio termina sin lanzar una excepción.
     */
    @AfterReturning("auditableOrderOperation(order)")
    public void logSuccessfulCompletion(order order) {
        // --- Auditoría de FIN (Éxito) ---
        log.info("--- Auditoría: Fin de proceso para {} ---", order.toString());
    }


    /**
     * (5) Advice @AfterThrowing: Manejo y Registro de Excepciones.
     * Se ejecuta si el método de negocio lanza CUALQUIER excepción.
     */
    @AfterThrowing(pointcut = "auditableOrderOperation(order)", throwing = "ex")
    public void handleException(order order, Throwable ex) {
        // --- Manejo de EXCEPCIONES ---
        log.error("[ERROR] {} falló: {} ({})", order.toString(), ex.getMessage(), "Error simulado");
        // Nota: La Auditoría de FIN NO se registra aquí, ya que el proceso falló.
    }
}
