package org.example.simulacionpedidos.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Anotación personalizada que marca métodos para ser interceptados por el Aspecto AOP,
 * separando la lógica de auditoría, rendimiento y manejo de excepciones.
 */
@Target(ElementType.METHOD) // Indica que esta anotación solo se puede usar en métodos.
@Retention(RetentionPolicy.RUNTIME) // CRUCIAL: Mantiene la anotación disponible en tiempo de ejecución para que AOP la vea.
public @interface Auditable {
    // No necesita atributos, su simple existencia es suficiente para el Pointcut.
}
