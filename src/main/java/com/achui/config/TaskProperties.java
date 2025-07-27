package com.achui.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Configuration
public class TaskProperties {

    private static final Logger logger = LoggerFactory.getLogger(TaskProperties.class);
    private List<TaskConfig> tasks = new ArrayList<>();

    @PostConstruct
    public void init() {
        loadTasksFromConfigFiles();
    }

    private void loadTasksFromConfigFiles() {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        try {
            Resource[] resources = resolver.getResources("classpath:config/*.yml");
            for (Resource resource : resources) {
                try (InputStream inputStream = resource.getInputStream()) {
                    TaskConfig taskConfig = mapper.readValue(inputStream, TaskConfig.class);
                    tasks.add(taskConfig);
                    logger.info("Loaded task '{}' from {}", taskConfig.getName(), resource.getFilename());
                } catch (IOException e) {
                    logger.error("Error loading task configuration from {}", resource.getFilename(), e);
                }
            }
        } catch (IOException e) {
            logger.error("Error scanning for task configuration files", e);
        }
    }

    public List<TaskConfig> getTasks() {
        return tasks;
    }

    // No longer need setTasks, as tasks are loaded internally

    public static class TaskConfig {
        private String name;
        private SftpConfig source;
        private SftpConfig target;
        private String sourcePath;
        private String sourceFormat;
        private String targetPath;
        private String targetFormat;
        private String templateName;
        private Map<String, Object> params;

        // Getters and setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public SftpConfig getSource() { return source; }
        public void setSource(SftpConfig source) { this.source = source; }
        public SftpConfig getTarget() { return target; }
        public void setTarget(SftpConfig target) { this.target = target; }
        public String getSourcePath() { return sourcePath; }
        public void setSourcePath(String sourcePath) { this.sourcePath = sourcePath; }
        public String getSourceFormat() { return sourceFormat; }
        public void setSourceFormat(String sourceFormat) { this.sourceFormat = sourceFormat; }
        public String getTargetPath() { return targetPath; }
        public void setTargetPath(String targetPath) { this.targetPath = targetPath; }
        public String getTargetFormat() { return targetFormat; }
        public void setTargetFormat(String targetFormat) { this.targetFormat = targetFormat; }
        public String getTemplateName() { return templateName; }
        public void setTemplateName(String templateName) { this.templateName = templateName; }
        public Map<String, Object> getParams() { return params; }
        public void setParams(Map<String, Object> params) { this.params = params; }
    }

    public static class SftpConfig {
        private String host;
        private int port;
        private String username;
        private String password;
        private String privateKeyPath;
        private String passphrase;

        // Getters and setters
        public String getHost() { return host; }
        public void setHost(String host) { this.host = host; }
        public int getPort() { return port; }
        public void setPort(int port) { this.port = port; }
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
        public String getPrivateKeyPath() { return privateKeyPath; }
        public void setPrivateKeyPath(String privateKeyPath) { this.privateKeyPath = privateKeyPath; }
        public String getPassphrase() { return passphrase; }
        public void setPassphrase(String passphrase) { this.passphrase = passphrase; }
    }
}
