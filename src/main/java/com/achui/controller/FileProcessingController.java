package com.achui.controller;

import com.achui.config.TaskProperties;
import com.achui.service.FileProcessingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/file-processing")
public class FileProcessingController {

    @Autowired
    private FileProcessingService fileProcessingService;

    @Autowired
    private TaskProperties taskProperties;

    @GetMapping("/trigger/{taskName}")
    public ResponseEntity<String> triggerTask(@PathVariable String taskName) {
        TaskProperties.TaskConfig taskConfig = taskProperties.getTasks().stream()
                .filter(t -> t.getName().equals(taskName))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Task not found: " + taskName));

        fileProcessingService.processFile(taskConfig);

        return ResponseEntity.ok("Task '" + taskName + "' triggered successfully.");
    }
}