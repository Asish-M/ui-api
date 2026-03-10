package com.automation.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

/**
 * JSON file reading utility using Jackson.
 */
public final class JsonUtil {

    private static final Logger LOG = LoggerUtil.getLogger(JsonUtil.class);
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private JsonUtil() {
        // utility class
    }

    /**
     * Read a JSON file into a list of maps.
     */
    public static List<Map<String, String>> readJsonAsList(String filePath) {
        try {
            Path path = Paths.get(filePath);
            byte[] bytes = Files.readAllBytes(path);
            return MAPPER.readValue(bytes, new TypeReference<>() {});
        } catch (IOException e) {
            LOG.error("Failed to read JSON file: {}", filePath, e);
            throw new RuntimeException("Cannot read JSON file: " + filePath, e);
        }
    }

    /**
     * Read a JSON file into a map.
     */
    public static Map<String, Object> readJsonAsMap(String filePath) {
        try {
            Path path = Paths.get(filePath);
            byte[] bytes = Files.readAllBytes(path);
            return MAPPER.readValue(bytes, new TypeReference<>() {});
        } catch (IOException e) {
            LOG.error("Failed to read JSON file: {}", filePath, e);
            throw new RuntimeException("Cannot read JSON file: " + filePath, e);
        }
    }

    /**
     * Read a JSON file into a POJO.
     */
    public static <T> T readJson(String filePath, Class<T> clazz) {
        try {
            Path path = Paths.get(filePath);
            byte[] bytes = Files.readAllBytes(path);
            return MAPPER.readValue(bytes, clazz);
        } catch (IOException e) {
            LOG.error("Failed to read JSON file: {}", filePath, e);
            throw new RuntimeException("Cannot read JSON file: " + filePath, e);
        }
    }

    /**
     * Read a JSON resource from classpath into a list of maps.
     */
    public static List<Map<String, String>> readJsonResourceAsList(String resourcePath) {
        try (InputStream is = JsonUtil.class.getClassLoader().getResourceAsStream(resourcePath)) {
            if (is == null) {
                throw new IOException("Resource not found: " + resourcePath);
            }
            return MAPPER.readValue(is, new TypeReference<>() {});
        } catch (IOException e) {
            LOG.error("Failed to read JSON resource: {}", resourcePath, e);
            throw new RuntimeException("Cannot read JSON resource: " + resourcePath, e);
        }
    }

    /**
     * Serialize an object to JSON string.
     */
    public static String toJson(Object obj) {
        try {
            return MAPPER.writeValueAsString(obj);
        } catch (IOException e) {
            LOG.error("Failed to serialize object to JSON", e);
            throw new RuntimeException("JSON serialization failed", e);
        }
    }
}
