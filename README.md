https://github.com/ZoeAcero/AlvaroMartin-DnielJose-ZoeAcero.git



# 🚀 Simulación de Procesamiento de Pedidos Concurrente con Spring AOP

Este proyecto implementa una simulación de un sistema de comercio electrónico que procesa pedidos de manera simultánea. La característica central es el uso de la **Programación Orientada a Aspectos (AOP)** para añadir funcionalidades transversales de forma modular, manteniendo el código de negocio completamente limpio.

## 👥 1. Datos de la Entrega

| Rol | Nombre Completo |
| :--- | :--- |
| **Miembro 1** | [Nombre del Estudiante 1 y Apellido(s)] |
| **Miembro 2** | [Nombre del Estudiante 2 y Apellido(s)] |
| **Miembro 3 (Opcional)** | [Nombre del Estudiante 3 y Apellido(s)] |

---

## 💡 2. Resumen de la Lógica y Diseño

### A. Lógica de Solución

El sistema simula 10 pedidos concurrentes. La aplicación está configurada como una web (`server.port=8081`) para visualizar los resultados.

1.  **Concurrencia:** Los 10 pedidos se lanzan simultáneamente mediante el método `@Async` en el `OrderProcessingService`, utilizando un `ThreadPoolTaskExecutor`. Esto garantiza que el procesamiento se realiza en hilos paralelos.
2.  **AOP (Separación de Preocupaciones):** Toda la auditoría, la medición de rendimiento y el manejo de errores se definen una sola vez en el `OrderAuditPerformanceAspect`, dejando al `OrderProcessingService` libre de código de logging o try-catch.
3.  **Visualización Segura:** Los hilos escriben sus resultados en un componente central y seguro (`SimulationState` con `CopyOnWriteArrayList`), lo que permite al `SimulationController` leer y mostrar los logs en la página web sin errores de concurrencia.

### B. Implementación del Aspecto (`OrderAuditPerformanceAspect`)

La clase `OrderAuditPerformanceAspect` es el corazón del diseño:

| Advice AOP | Función Específica | Objetivo Logrado |
| :--- | :--- | :--- |
| **`@Pointcut`** | Define el punto de corte (`execution`) en cualquier método marcado con la anotación `@Auditable`. | Centraliza el objetivo de la interceptación. |
| **`@Around`** | Envuelve el método, registra el **INICIO** y calcula el **TIEMPO DE EJECUCIÓN** (`[PERFORMANCE]`) en el bloque `finally`. | Garantiza la medición de rendimiento, incluso si el proceso falla. |
| **`@AfterThrowing`** | Captura la `RuntimeException` (errores simulados como "Pago rechazado") y registra el **ERROR** en el log. | Separa la gestión del error de la lógica de negocio. |
| **`@AfterReturning`** | Se ejecuta solo si el método termina con éxito (sin excepción), registrando el **FIN** de la auditoría. | Mantiene la auditoría precisa, distinguiendo entre procesos finalizados y fallidos. |

---

## 📁 3. Estructura y Descripción de Archivos Clave

| Archivo | Ubicación | Descripción Funcional |
| :--- | :--- | :--- |
| `SimulacionpedidosApplication.java` | Base | Arranca la aplicación Spring Boot. Es el punto de inicio que habilita `@EnableAsync`. |
| `application.properties` | `resources/` | Archivo de configuración que establece `server.port=8081` para evitar colisiones con otras aplicaciones. |
| `Auditable.java` | `annotations/` | **Anotación Marcadora.** Etiqueta que indica al Aspecto qué métodos deben ser intervenidos. |
| `Order.java` | `orders/` | Modelo de datos inmutable que representa un pedido. |
| `OrderProcessingService.java` | `service/` | **El Negocio.** Contiene el método `@Async @Auditable processOrder` con la lógica de simulación (`Thread.sleep`) y el lanzamiento de excepciones aleatorias. |
| `SimulationState.java` | `service/` | **Estado Concurrente.** Almacenamiento central y seguro para los logs generados por los hilos paralelos. |
| `OrderAuditPerformanceAspect.java` | `aspects/` | **El Interceptor.** Implementa toda la lógica transversal de auditoría, rendimiento y manejo de excepciones mediante AOP. |
| `SimulationController.java` | `controller/` | **Web Controller.** Lanza la simulación y extrae los logs de `SimulationState` para inyectarlos en la plantilla HTML. |
| `simulation.html` | `resources/templates/` | Plantilla Thymeleaf que renderiza los logs de auditoría y rendimiento en una vista web estructurada. |
