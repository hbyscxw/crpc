package com.cxw.crpc.thread;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 * @author chengxuwei
 */
public class NameThreadFactory implements ThreadFactory {
    private static final AtomicInteger poolNumber = new AtomicInteger(1);
    private final AtomicInteger threadNumber = new AtomicInteger(1);
    private ThreadGroup threadGroup;
    private String namePrefix;
    private boolean daemon;
    private int priority;
    private String name;

    public NameThreadFactory(String name, boolean daemon, int priority) {

        if (priority > Thread.MAX_PRIORITY || priority<Thread.MIN_PRIORITY) {
            throw new IllegalArgumentException("priority: " + priority + " (expected: Thread.MIN_PRIORITY <= priority <= Thread.MAX_PRIORITY)");
        }

        this.name = name;
        this.daemon = daemon;
        this.priority = priority;
        SecurityManager sm = System.getSecurityManager();
        this.threadGroup = sm != null ? sm.getThreadGroup() : Thread.currentThread().getThreadGroup();
        this.namePrefix = "pool-" + poolNumber.getAndIncrement() + "-" + name;
    }

    public NameThreadFactory(String name) {
        this(name, false, Thread.NORM_PRIORITY);
    }

    public NameThreadFactory(String name, boolean daemon) {
       this(name, daemon, Thread.NORM_PRIORITY);
    }

    @Override
    public Thread newThread(Runnable r) {
        Thread t = new Thread(this.threadGroup, r, this.namePrefix + "-" + this.threadNumber.getAndIncrement(), 0);
        if (t.isDaemon()) {
            if (!this.daemon) {
                t.setDaemon(false);
            }
        }

        if (t.getPriority() != this.priority) {
            t.setPriority(Thread.NORM_PRIORITY);
        }

        return t;
    }

    public String getName() {
        return name;
    }
}
