package org.zollty.tool;

import java.util.Arrays;
import java.util.Date;

import org.jretty.util.DateFormatUtils;
import org.jretty.util.NamedRunnable;
import org.jretty.util.ThreadUtils;

public class MultiTaskTest {

    public static void main(String[] args) {

        MultiTaskScheduler mts = new MultiTaskScheduler(Arrays.asList(new TimedTask(new TestTask(), 5000)));

        mts.addTask(new TestTask("abcdef"), 2000);

        mts.start();

        ThreadUtils.sleepThread(60000);

        mts.shutdown();
    }

    public static class TestTask implements NamedRunnable {

        private String name;

        public TestTask() {
        }

        public TestTask(String name) {
            this.name = name;
        }

        @Override
        public void run() {
            System.out.println(getName() + "run......" + DateFormatUtils.format_yyyy_MM_dd_HH_mm_ss(new Date()));
        }

        @Override
        public String getName() {

            return name == null ? "TestTask" : name;
        }

    }

}