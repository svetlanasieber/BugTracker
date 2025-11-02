package com.bugtracker.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.util.ErrorHandler;

@Configuration
@EnableScheduling
@Slf4j
public class SchedulingConfig {
    @Bean
    public TaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(5);
        scheduler.setThreadNamePrefix("bugtracker-scheduled-");
        scheduler.setAwaitTerminationSeconds(30);
        scheduler.setWaitForTasksToCompleteOnShutdown(true);
        scheduler.setErrorHandler(new LoggingErrorHandler());
        scheduler.initialize();
        log.info("TaskScheduler initialized with pool size: 5");
        return scheduler;
    }
    public static class LoggingErrorHandler implements ErrorHandler {
        @Override
        public void handleError(@org.springframework.lang.NonNull Throwable t) {
            log.error("Error in scheduled task: {}", t.getMessage(), t);
        }
    }
}

