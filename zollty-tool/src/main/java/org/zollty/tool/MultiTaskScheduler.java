package org.zollty.tool;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.jretty.log.LogFactory;
import org.jretty.log.Logger;
import org.jretty.util.NamedThreadFactory;
import org.jretty.util.ThreadUtils;

/**
 * 相当于一个定时器，到了一定时间就会异步执行某个任务。 <br>
 * 相比ScheduledExecutorService，它占用内存更少，代码简单得多。
 * 
 * @author zollty
 * @since 2013-12-13
 */
public class MultiTaskScheduler extends Thread {

    private static final Logger LOG = LogFactory.getLogger(MultiTaskScheduler.class);

    /**
     * true--启用，false--禁用，主要是控制线程内的while循环 <br>
     * 加 volatile关键字，以便其值变化时，能够及时更新到共享内存中
     */
    private volatile boolean enabled = true;

    private static final long CHECK_INTERVAL = 500L; // 0.5秒检查一次

    private ExecutorService executor;

    private List<TimedTask> tasks = new LinkedList<TimedTask>();

    public MultiTaskScheduler() {
    }

    public MultiTaskScheduler(List<TimedTask> tasks) {
        this.setTasks(tasks);
    }

    public boolean addTask(TimedTask task) {
        return tasks.add(task);
    }
    
    public boolean addTask(Runnable task, long interval) {
        return tasks.add(new TimedTask(task, interval));
    }

    public boolean addTask(Runnable task, long interval, String name) {
        return tasks.add(new TimedTask(task, interval, name));
    }

    public boolean addTask(List<TimedTask> tasks) {
        return this.tasks.addAll(tasks);
    }

    public boolean removeTask(TimedTask task) {
        return tasks.remove(task);
    }

    public void setTasks(List<TimedTask> tasks) {
        this.tasks.addAll(tasks);
    }

    public List<TimedTask> getTasks() {
        return tasks;
    }

    public void clearTask() {
        tasks.clear();
    }

    @Override
    public void run() {
        executor = Executors.newCachedThreadPool(new NamedThreadFactory("MultiTimerTask"));
        LOG.info(MultiTaskScheduler.class.getSimpleName() + " Thread Starts Run...");
        LOG.info("there are {} tasks in {}!", tasks.size(), MultiTaskScheduler.class.getSimpleName());
        while (enabled) {
            long now = System.currentTimeMillis();
            for (TimedTask tb : tasks) {
                if (now - tb.getBegin() > tb.getInterval()) {
                    LOG.info("start to execute [{}] thread, interval time {}!", tb.getName(), tb.getInterval());
                    executor.execute(tb.getTask());
                    tb.setBegin(now);
                }
            }
            ThreadUtils.sleepThread(CHECK_INTERVAL);
        }
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void shutdown() {
        LOG.info(MultiTaskScheduler.class.getSimpleName() + " Thread Stop...");
        this.enabled = false;
        this.interrupt();
        try {
            executor.shutdownNow();
        } catch (Exception e) {
            LOG.warn(e);
        }
    }

}