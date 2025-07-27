package com.achui.service;

import com.achui.client.KeyBasedSftpClient;
import com.achui.client.PasswordSftpClient;
import com.achui.client.SftpClient;
import com.achui.config.TaskProperties;
import com.achui.processor.DataProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.Map;

@Service
public class FileProcessingService {
    private static final Logger logger = LoggerFactory.getLogger(FileProcessingService.class);

    @Autowired
    private DataProcessor dataProcessor;

    public void processFile(TaskProperties.TaskConfig taskConfig) {
        SftpClient sourceClient = createSftpClient(taskConfig.getSource());
        SftpClient targetClient = createSftpClient(taskConfig.getTarget());

        try {
            sourceClient.connect();
            InputStream sourceData = sourceClient.download(taskConfig.getSourcePath());

            Map<String, Object> processedData = dataProcessor.process(sourceData, taskConfig.getSourceFormat(), taskConfig.getParams());

            InputStream transformedData = dataProcessor.transform(processedData, taskConfig.getTargetFormat(), taskConfig.getTemplateName());

            targetClient.connect();
            targetClient.upload(taskConfig.getTargetPath(), transformedData);

            logger.info("File processing for task '{}' completed successfully", taskConfig.getName());
        } catch (Exception e) {
            logger.error("Error processing file for task '{}': {}", taskConfig.getName(), e.getMessage(), e);
            throw new RuntimeException("File processing failed for task: " + taskConfig.getName(), e);
        } finally {
            sourceClient.disconnect();
            targetClient.disconnect();
        }
    }

    private SftpClient createSftpClient(TaskProperties.SftpConfig config) {
        if (config.getPrivateKeyPath() != null) {
            return new KeyBasedSftpClient(
                    config.getHost(),
                    config.getPort(),
                    config.getUsername(),
                    config.getPrivateKeyPath(),
                    config.getPassphrase()
            );
        } else {
            return new PasswordSftpClient(
                    config.getHost(),
                    config.getPort(),
                    config.getUsername(),
                    config.getPassword()
            );
        }
    }
}