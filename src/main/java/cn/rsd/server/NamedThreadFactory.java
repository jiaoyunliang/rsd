package cn.rsd.server;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author 焦云亮
 * @date 2018/10/29
 * @modifyUser
 * @modifyDate
 */
// 自定义线程工厂类
public class NamedThreadFactory implements ThreadFactory {
    // a thread counter
    private static AtomicInteger counter = new AtomicInteger(1);
    private String        name = "Adam";
    private boolean       daemon; // 守护线程
    private int           priority; // 线程优先级

    public NamedThreadFactory(String name) {
        this(name, false, -1);
    }

    public NamedThreadFactory(String name, boolean daemon) {
        this(name, daemon, -1);
    }

    public NamedThreadFactory(String name, boolean daemon, int priority) {
        this.name = name;
        this.daemon = daemon;
        this.priority = priority;
    }

    @Override
    public Thread newThread(Runnable r) {
        Thread thread = new Thread(r, name + "[" + counter.getAndIncrement() + "]");
        thread.setDaemon(daemon);
        if (priority != -1) {
            thread.setPriority(priority);
        }
        return thread;
    }
}