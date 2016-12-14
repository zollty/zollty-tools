package org.jretty.util;

/**
 * 可定义名字的Runnable
 * 
 * @author zollty
 * @since 2016-9-03
 */
public interface NamedRunnable extends Runnable {
    
    public String getName();

}
