package org.jretty.util.httpclient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.jretty.util.HttpClientStaticBuilder;
import org.jretty.util.NamedThreadFactory;

/**
 * 多线程，多IP目标，每个线程往任意IP发送请求。
 * 
 * @see Readme.java
 * 
 * @author zollty
 * @since 2017-1-13
 */
public class Demo04 {
    
    static void post(CloseableHttpClient httpclient, String url) throws ClientProtocolException, IOException {
        long start = System.currentTimeMillis();
        HttpPost httpPost = new HttpPost(url);
        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        nvps.add(new BasicNameValuePair("username", "vip"));
        nvps.add(new BasicNameValuePair("password", "secret"));
        httpPost.setEntity(new UrlEncodedFormEntity(nvps));
        CloseableHttpResponse response = httpclient.execute(httpPost);

        try {
            System.out.println(response.getStatusLine());
            HttpEntity entity = response.getEntity();
            // do something useful with the response body
            // and ensure it is fully consumed
            EntityUtils.consume(entity);
        } finally {
            response.close();
            System.out.println("cost time2: " + (System.currentTimeMillis() - start));
        }
    }
    
    public static void main(String[] args) {
        int singleIpThreadNum = 50;
        long start = System.currentTimeMillis();
        final CloseableHttpClient httpclient = HttpClientStaticBuilder.build(null, null, null, (int)(singleIpThreadNum*1.5), 150);
        System.out.println("cost time1: " + (System.currentTimeMillis() - start));
        
        final String url = "http://httpbin.org/post";
        ExecutorService es = Executors.newFixedThreadPool(singleIpThreadNum, 
                new NamedThreadFactory("HttpClientDemo4", true));
        Runnable r = new Runnable() {
            
            @Override
            public void run() {
                try {
                    post(httpclient, url);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        start = System.currentTimeMillis();
        
        LinkedList<Future> rFutures = new LinkedList<Future>();
        for (int i = 0; i < singleIpThreadNum; i++) {
            rFutures.add(es.submit(r));
        }
        
        wait2Shutdown(rFutures, es);
        try {
            httpclient.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("cost time0: " + (System.currentTimeMillis() - start));
    }
    
    static void wait2Shutdown(LinkedList<Future> rFutures, ExecutorService es) {
        while (!rFutures.isEmpty()) {
            try {
                rFutures.poll().get();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        es.shutdownNow();
    }

}
