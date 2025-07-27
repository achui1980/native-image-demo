package com.achui.scheduler;

import com.achui.config.TaskProperties;
import com.achui.service.FileProcessingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component("fileProcessingTaskScheduler")
public class TaskScheduler {
    private static final Logger logger = LoggerFactory.getLogger(TaskScheduler.class);

    @Autowired
    private FileProcessingService fileProcessingService;

    @Autowired
    private TaskProperties taskProperties;

    @Scheduled(cron = "${app.scheduler.cron:0 0 * * * *}")
    public void scheduleFileProcessingTask() {
        logger.info("Starting scheduled file processing tasks...");
        taskProperties.getTasks().forEach(task -> {
            try {
                fileProcessingService.processFile(task);
            } catch (Exception e) {
                logger.error("Error processing scheduled task '{}': {}", task.getName(), e.getMessage(), e);
            }
        });
        logger.info("Scheduled file processing tasks finished.");
    }
}