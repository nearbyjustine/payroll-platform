package dev.justine.payroll.config;

import java.util.Map;
import org.slf4j.MDC;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskDecorator;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableAsync
@EnableCaching
@EnableConfigurationProperties(AppProperties.class)
public class AsyncConfig {

    /**
     * A bounded pool for payroll processing. Unbounded pools (or new Thread per task) are how
     * servers run out of memory under load. Queue full -> caller gets an error instead of the app dying.
     */
    @Bean(name = "payrollExecutor")
    ThreadPoolTaskExecutor payrollExecutor() {
        ThreadPoolTaskExecutor ex = new ThreadPoolTaskExecutor();
        ex.setCorePoolSize(2);
        ex.setMaxPoolSize(4);
        ex.setQueueCapacity(20);
        ex.setThreadNamePrefix("payroll-");
        ex.setTaskDecorator(mdcPropagation());
        ex.setWaitForTasksToCompleteOnShutdown(true);
        ex.setAwaitTerminationSeconds(30);
        return ex;
    }

    /** Copies the logging MDC (correlation ID) onto the worker thread, since thread-locals don't cross threads. */
    private TaskDecorator mdcPropagation() {
        return task -> {
            Map<String, String> context = MDC.getCopyOfContextMap();
            return () -> {
                if (context != null) MDC.setContextMap(context);
                try {
                    task.run();
                } finally {
                    MDC.clear();
                }
            };
        };
    }
}
