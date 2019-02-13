package org.jretty.util.httpclient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.Configurable;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.jretty.util.HttpClientStaticBuilder;

/**
 * 偶尔执行一次，往多个IP发送请求。
 * 
 * @see Readme.java
 * 
 * @author zollty
 * @since 2017-1-11
 */
public class Demo01 {

    static void get() throws IOException {
        long start = System.currentTimeMillis();
        CloseableHttpClient httpclient = HttpClientStaticBuilder.buildDefault();
        System.out.println("cost time1: " + (System.currentTimeMillis() - start));
        start = System.currentTimeMillis();
        try {
            HttpGet httpget = new HttpGet("http://httpbin.org/get");
            // Request configuration can be overridden at the request level.
            // They will take precedence over the one set at the client level.
            RequestConfig requestConfig = RequestConfig.copy(((Configurable) httpclient).getConfig())
                    .setSocketTimeout(5000).setConnectTimeout(2000).setConnectionRequestTimeout(1000).build();
            httpget.setConfig(requestConfig);

            System.out.println("executing request " + httpget.getURI());
            CloseableHttpResponse response = httpclient.execute(httpget);
            try {
                System.out.println("----------------------------------------");
                System.out.println(response.getStatusLine());
                HttpEntity entity = response.getEntity();
                System.out.println(EntityUtils.toString(entity));
                System.out.println("----------------------------------------");

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

    static void post() throws ClientProtocolException, IOException {
        long start = System.currentTimeMillis();
        CloseableHttpClient httpclient = HttpClientStaticBuilder.buildDefault();
        System.out.println("cost time1: " + (System.currentTimeMillis() - start));
        start = System.currentTimeMillis();
        try {
            HttpPost httpPost = new HttpPost("http://httpbin.org/post");
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
        } finally {
            httpclient.close();
        }
    }

    public static void main(String[] args) throws Exception {
        get();
    }

}