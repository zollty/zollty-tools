/**
 * 
 */
package org.zollty.tool;

import java.util.LinkedList;
import java.util.List;

import org.jretty.log.LogFactory;
import org.jretty.log.Logger;
import org.zollty.tool.cache.JrettyCache;

/**
 * 
 * @author zollty
 * @since 2018年3月6日
 */
public class CacheManagerTest2 {
    
    private static final Logger LOG = LogFactory.getLogger(CacheManagerTest2.class);
    
    public static void main(String[] args) throws InterruptedException {
        final String key = "zollty";
        final String value = "daaaa";
        final JrettyCache cache = new JrettyCache(10, 4000);
        
        List<TimedTask> tasks = new LinkedList<TimedTask>();
        for(int i=0; i<10; i++) {
            tasks.add(new TimedTask(new Runnable() {
                @Override
                public void run() {
                    if(cache.putIfAbsent(key, value)) {
                        LOG.error("-----------log.............");
                    } 
                }
            }, 100));
        }
        
        MultiTaskScheduler she = new MultiTaskScheduler();
        
        she.setTasks(tasks);
        
        she.start();
        
        Thread.sleep(21000L);
        
        she.shutdown();
        
    }

}
