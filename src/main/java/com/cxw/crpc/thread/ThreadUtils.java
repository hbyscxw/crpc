package com.cxw.crpc.thread;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.*;

/**
 * @author chengxuwei
 */
public class ThreadUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(ThreadUtils.class);
    private static volatile StandardThreadExecutor standardExecutor = null;
    private static final int CORE_SIZE = Runtime.getRuntime().availableProcessors();
    private static final int MAX_SIZE = 5 * CORE_SIZE;
    private static final int LARGEST_SIZE = 200;
    private static final int QUEUE_SIZE = 20000;
    private static final int KEEP_ALIVE_TIME = 60;


    //兼容submit方法
    static {
        int maxSize = MAX_SIZE;
        if (MAX_SIZE > LARGEST_SIZE) {
            maxSize = LARGEST_SIZE;
        }
        maxSize = getMaxThreadSize();
        int coreSize = 2 * CORE_SIZE;
        LOGGER.info("standardExecutor线程池信息,核心线程数=[{}],最大线程数=[{}],空闲有效时间=[{}],队列容量=[{}]",
                coreSize, maxSize, KEEP_ALIVE_TIME, QUEUE_SIZE);
        standardExecutor = new StandardThreadExecutor(coreSize, maxSize, KEEP_ALIVE_TIME, TimeUnit.SECONDS,
                QUEUE_SIZE, new NameThreadFactory("stand-thread-common"));
        standardExecutor.allowCoreThreadTimeOut(true);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> shutDownStand()));
    }

    /**
     * io密集型
     *
     * @return
     */
    public static StandardThreadExecutor getStandardExecutorInstance() {
        return standardExecutor;
    }


    public static void shutDownStand() {
        LOGGER.info("关闭线程池...");
        if (standardExecutor != null) {
            standardExecutor.shutdown();
        }
    }

    /**
     * 最大线程数
     *
     * @return
     */
    private static int getMaxThreadSize() {
        int maxSize = MAX_SIZE;
        if (MAX_SIZE > LARGEST_SIZE) {
            maxSize = LARGEST_SIZE;
        }
        return maxSize;
    }
}
