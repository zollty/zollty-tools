package org.zollty.tool;

import java.io.Serializable;

import org.jretty.util.NamedRunnable;

public class TimedTask implements Serializable {

    private static final long serialVersionUID = -8590946750368073216L;

    private String name;
    private long begin;
    private long interval;
    private Runnable task;
    
    public TimedTask() {
    }
    
    public TimedTask(Runnable task, long interval) {
        super();
        this.begin = System.currentTimeMillis();
        this.interval = interval;
        this.task = task;
    }

    public TimedTask(Runnable task, long interval, String name) {
        super();
        this.name = name;
        this.begin = System.currentTimeMillis();
        this.interval = interval;
        this.task = task;
    }

    public String getName() {
        if (name != null) {
            return name;
        }
        if (task instanceof NamedRunnable) {
            name = ((NamedRunnable) task).getName();
        }
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getBegin() {
        return begin;
    }

    public void setBegin(long begin) {
        this.begin = begin;
    }

    public long getInterval() {
        return interval;
    }

    public void setInterval(long interval) {
        this.interval = interval;
    }

    public Runnable getTask() {
        return task;
    }

    public void setTask(Runnable task) {
        this.task = task;
    }

}