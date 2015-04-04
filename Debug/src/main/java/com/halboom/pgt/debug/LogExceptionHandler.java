package com.halboom.pgt.debug;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created with IntelliJ IDEA.
 * User: MW
 * Date: 3/4/13
 * Time: 1:23 PM
 * Handles exceptions not caught and logs them in the logger.
 */
public class LogExceptionHandler implements Thread.UncaughtExceptionHandler {
    /**
     * Logger to log with.
     */
    private static Logger log = LoggerFactory.getLogger(LogExceptionHandler.class);

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        log.error("Uncaught exception in thread: " + t.getName(), e);
    }
}
