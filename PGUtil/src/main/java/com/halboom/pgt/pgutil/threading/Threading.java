package com.halboom.pgt.pgutil.threading;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;

/**
 * Created with IntelliJ IDEA.
 * User: MW
 * Date: 1/29/13
 * Time: 1:41 PM
 * Handles threads for the game.
 */
public final class Threading {
    /**
     * Singleton instance.
     */
    private static Threading ourInstance = new Threading();

    /**
     * @return the singleton instance.
     */
    public static Threading getInstance() {
        return ourInstance;
    }

    /**
     * Maximum number of threads.
     */
    private static final int THREADS = 4;

    /**
     * Executor for the threads.
     */
    private ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(THREADS);

    /**
     * Initializes the class.
     */
    private Threading() {
    }

    /**
     * Destroys the threads.
     */
    public void destroy() {
        executor.shutdownNow();
    }

    /**
     * @return the executor for the threads.
     */
    public ExecutorService getExecutor() {
        return executor;
    }
}
