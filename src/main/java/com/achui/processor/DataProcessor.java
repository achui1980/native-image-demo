package com.achui.processor;

import java.io.InputStream;
import java.util.Map;

public interface DataProcessor {
    /**
     * 处理输入流中的数据
     * @param inputStream 输入数据流
     * @param format 数据格式（如CSV、XML等）
     * @param params 处理参数
     * @return 处理后的数据Map
     */
    Map<String, Object> process(InputStream inputStream, String format, Map<String, Object> params) throws Exception;

    /**
     * 将处理后的数据转换为指定格式
     * @param data 处理后的数据
     * @param targetFormat 目标格式（如JSON、CSV、XML等）
     * @param templateName 转换模板名称
     * @return 转换后的数据流
     */
    InputStream transform(Map<String, Object> data, String targetFormat, String templateName) throws Exception;
}