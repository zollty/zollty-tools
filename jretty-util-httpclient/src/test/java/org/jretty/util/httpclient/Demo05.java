package org.jretty.util.httpclient;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.StandardHttpRequestRetryHandler;
import org.apache.http.util.EntityUtils;
import org.jretty.util.HttpClientStaticBuilder;

/**
 * ！无条件允许“等幂”请求（GET, HEAD, PUT, DELETE, OPTIONS, and TRACE）失败重试！（即使对方服务器500）
 * 
 * 不建议这么用。
 * 
 * @see Readme.java
 * 
 * @author zollty
 * @since 2017-1-13
 */
public class Demo05 {
    
    static void post(CloseableHttpClient httpclient, String url) throws ClientProtocolException, IOException {
        long start = System.currentTimeMillis();
        try {
            HttpGet httpGet = new HttpGet(url);
            CloseableHttpResponse response = httpclient.execute(httpGet);

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
        } finally {
            httpclient.close();
        }
    }

    public static void main(String[] args) throws Exception {
        long start = System.currentTimeMillis();
        final HttpRequestRetryHandler retryHandler = new StandardHttpRequestRetryHandler();
        CloseableHttpClient httpclient = HttpClientStaticBuilder.custom(1000, 3000, null, null, null)
                .setRetryHandler(retryHandler)
                .build();
        System.out.println("cost time1: " + (System.currentTimeMillis() - start));
        post(httpclient, "https://www.github.com/post");
    }

}
