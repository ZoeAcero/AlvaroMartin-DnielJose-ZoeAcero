package org.example.simulacionpedidos.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class TaskExecutorBeanTest {

    @Autowired
    @Qualifier("taskExecutor")
    private TaskExecutor taskExecutor;

    @Test
    void taskExecutorBeanPresentAndConfigured() {
        assertNotNull(taskExecutor, "Debe existir el bean 'taskExecutor'");
        assertTrue(taskExecutor instanceof ThreadPoolTaskExecutor, "Debe ser ThreadPoolTaskExecutor");
        ThreadPoolTaskExecutor ex = (ThreadPoolTaskExecutor) taskExecutor;
        assertTrue(ex.getCorePoolSize() >= 1);
        assertTrue(ex.getMaxPoolSize() >= ex.getCorePoolSize());
        assertNotNull(ex.getThreadNamePrefix());
    }
}