package com.achui.processor;

import freemarker.template.Configuration;
import freemarker.template.Template;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class DefaultDataProcessor implements DataProcessor {
    private static final Logger logger = LoggerFactory.getLogger(DefaultDataProcessor.class);

    @Autowired
    private Configuration freemarkerConfig;

    @Override
    public Map<String, Object> process(InputStream inputStream, String format, Map<String, Object> params) throws Exception {
        Map<String, Object> result = new HashMap<>();
        
        switch (format.toLowerCase()) {
            case "csv":
                result = processCsv(inputStream, params);
                break;
            case "xml":
                result = processXml(inputStream, params);
                break;
            default:
                throw new UnsupportedOperationException("Unsupported format: " + format);
        }

        return result;
    }

    @Override
    public InputStream transform(Map<String, Object> data, String targetFormat, String templateName) throws Exception {
        Template template = freemarkerConfig.getTemplate(templateName);
        StringWriter writer = new StringWriter();
        template.process(data, writer);

        return new ByteArrayInputStream(writer.toString().getBytes(StandardCharsets.UTF_8));
    }

    private Map<String, Object> processCsv(InputStream inputStream, Map<String, Object> params) throws IOException {
        Map<String, Object> result = new HashMap<>();
        List<Map<String, String>> records = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            String[] headers = reader.readLine().split(",");
            String line;
            while ((line = reader.readLine()) != null) {
                String[] values = line.split(",");
                Map<String, String> record = new HashMap<>();
                for (int i = 0; i < headers.length && i < values.length; i++) {
                    record.put(headers[i].trim(), values[i].trim());
                }
                records.add(record);
            }
        }

        result.put("records", records);
        return result;
    }

    private Map<String, Object> processXml(InputStream inputStream, Map<String, Object> params) throws Exception {
        Map<String, Object> result = new HashMap<>();
        List<Map<String, String>> records = new ArrayList<>();

        XMLInputFactory factory = XMLInputFactory.newInstance();
        XMLStreamReader reader = factory.createXMLStreamReader(inputStream);

        Map<String, String> currentRecord = null;
        String currentElement = null;

        while (reader.hasNext()) {
            int event = reader.next();

            switch (event) {
                case XMLStreamReader.START_ELEMENT:
                    currentElement = reader.getLocalName();
                    if ("record".equals(currentElement)) {
                        currentRecord = new HashMap<>();
                    }
                    break;

                case XMLStreamReader.CHARACTERS:
                    if (currentRecord != null && currentElement != null) {
                        String value = reader.getText().trim();
                        if (!value.isEmpty()) {
                            currentRecord.put(currentElement, value);
                        }
                    }
                    break;

                case XMLStreamReader.END_ELEMENT:
                    if ("record".equals(reader.getLocalName()) && currentRecord != null) {
                        records.add(currentRecord);
                        currentRecord = null;
                    }
                    currentElement = null;
                    break;
            }
        }

        result.put("records", records);
        return result;
    }
}