package org.jretty.util;

import java.util.concurrent.ThreadFactory;

public class NamedThreadFactory implements ThreadFactory {

    private String baseName;
    private boolean addTimestamps;

    public NamedThreadFactory(String baseName) {
        super();
        this.baseName = baseName;
        this.addTimestamps = true;
    }

    public NamedThreadFactory(String baseName, boolean addTimestamps) {
        super();
        this.baseName = baseName;
        this.addTimestamps = addTimestamps;
    }

    @Override
    public Thread newThread(Runnable r) {
        String name;
        if (r instanceof NamedRunnable) {
            name = addTimestamps
                    ? ((NamedRunnable) r).getName() + "-" + DateFormatUtils.getUniqueDatePattern_TimeMillis_noSplit()
                    : ((NamedRunnable) r).getName();
        } else {
            name = baseName + "$" + DateFormatUtils.getUniqueDatePattern_TimeMillis_noSplit();
        }

        return new Thread(r, name);
    }

    public String getBaseName() {
        return baseName;
    }

    public void setBaseName(String baseName) {
        this.baseName = baseName;
    }

}