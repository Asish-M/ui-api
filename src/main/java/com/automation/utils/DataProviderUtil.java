package com.automation.utils;

import org.apache.logging.log4j.Logger;
import org.testng.annotations.DataProvider;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

/**
 * TestNG DataProvider that reads test data from JSON files.
 * <p>
 * Convention: the JSON file is resolved by test method name.
 * File path: {@code src/test/resources/testdata/<methodName>.json}
 * <p>
 * Each JSON file should be an array of objects, e.g.:
 * <pre>
 * [
 *   { "username": "tomsmith", "password": "SuperSecretPassword!" },
 *   { "username": "invalid",  "password": "wrong" }
 * ]
 * </pre>
 */
public class DataProviderUtil {

    private static final Logger LOG = LoggerUtil.getLogger(DataProviderUtil.class);
    private static final String TEST_DATA_DIR = "src/test/resources/testdata/";

    @DataProvider(name = "jsonDataProvider")
    public static Object[][] jsonDataProvider(Method method) {
        String fileName = TEST_DATA_DIR + method.getName() + ".json";
        LOG.info("Loading test data from: {}", fileName);
        List<Map<String, String>> data = JsonUtil.readJsonAsList(fileName);
        return data.stream()
                .map(map -> new Object[]{map})
                .toArray(Object[][]::new);
    }

    /**
     * Load data from a specific JSON file (not tied to method name).
     */
    public static Object[][] fromFile(String filePath) {
        List<Map<String, String>> data = JsonUtil.readJsonAsList(filePath);
        return data.stream()
                .map(map -> new Object[]{map})
                .toArray(Object[][]::new);
    }
}
