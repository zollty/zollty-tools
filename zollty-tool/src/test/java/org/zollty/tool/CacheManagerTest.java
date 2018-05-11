/**
 * 
 */
package org.zollty.tool;

import org.zollty.tool.cache.JrettyCache;

/**
 * 
 * @author zollty
 * @since 2018年3月6日
 */
public class CacheManagerTest {

    public static void main(String[] args) throws Exception {
        
        String key = "zollty";
        String value = "daaaa";
        JrettyCache cache = new JrettyCache(10, 1000);
//        cache.put(key, "daaaa");
        
        if(cache.putIfAbsent(key, value)) {
            System.out.println("log.............");
        } else {
            System.out.println("[null]");
        }
        if(cache.putIfAbsent(key, value)) {
            System.out.println("log.............");
        } else {
            System.out.println("[null]");
        }
        if(cache.putIfAbsent(key+"0", value)) {
            System.out.println("log.............");
        } else {
            System.out.println("[null]");
        }
        
//        System.out.println("1:" + cache.exits(key+"1"));
//        System.out.println(cache.exits(key));
//        System.out.println((String)cache.get(key));
//        System.out.println(cache.size());

        Thread.sleep(2 * 1000);
//        System.out.println("2:" + cache.exits(key+"2"));
//        System.out.println(cache.exits(key));
//        System.out.println((String)cache.get(key));
//        System.out.println(cache.size());
        System.out.println("===========================================");
        
        if(cache.putIfAbsent(key, value)) {
            System.out.println("log.............");
        } else {
            System.out.println("[null]");
        }
        if(cache.putIfAbsent(key, value)) {
            System.out.println("log.............");
        } else {
            System.out.println("[null]");
        }
        if(cache.putIfAbsent(key+"0", value)) {
            System.out.println("log.............");
        } else {
            System.out.println("[null]");
        }
    }

}
