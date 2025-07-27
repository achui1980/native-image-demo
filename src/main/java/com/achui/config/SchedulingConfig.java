package com.achui.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
public class SchedulingConfig {
    // 定时任务配置将通过application.yml进行配置
    // 具体的定时任务实现将在TaskScheduler中完成
}