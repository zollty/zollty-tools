package org.jretty.util.httpclient;

import org.apache.http.impl.client.CloseableHttpClient;
import org.jretty.util.HttpClientStaticBuilder;

/**
 * 多线程，多IP目标，但每个IP只有一个线程IP发送请求。
 * 
 * @see Readme.java
 * 
 * @author zollty
 * @since 2017-1-13
 */
public class Demo03 {
    

    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        final CloseableHttpClient httpclient = HttpClientStaticBuilder.build(null, null, null, null, 100);
        System.out.println("cost time1: " + (System.currentTimeMillis() - start));
    }

}
