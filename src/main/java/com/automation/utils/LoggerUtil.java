package com.automation.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Thin wrapper around Log4j2 for consistent logger creation.
 *
 * <pre>
 *   private static final Logger LOG = LoggerUtil.getLogger(MyClass.class);
 * </pre>
 */
public final class LoggerUtil {

    private LoggerUtil() {
        // utility class
    }

    /**
     * Returns a Log4j2 {@link Logger} for the given class.
     */
    public static Logger getLogger(Class<?> clazz) {
        return LogManager.getLogger(clazz);
    }
}
