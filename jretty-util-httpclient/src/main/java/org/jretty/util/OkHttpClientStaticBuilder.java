package org.jretty.util;

import java.time.Duration;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

import okhttp3.ConnectionPool;
import okhttp3.Dispatcher;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;

/**
 * 
 * @author zollty
 * @since 2021年9月16日
 */
public class OkHttpClientStaticBuilder {
    /*
    with spring, you can use:
     <bean id="okHttpClient" class="org.jretty.util.OkHttpClientStaticBuilder"
       factory-method="build" destroy-method="close">
       <constructor-arg index="0"><null/></constructor-arg>
       <constructor-arg index="1"><null/></constructor-arg>
       <constructor-arg index="2"><null/></constructor-arg>
       <constructor-arg index="3"><null/></constructor-arg>
       <constructor-arg index="4"><null/></constructor-arg>
     </bean>
    */
    
    /**
     * @see #custom(Integer, Integer, Integer, Integer, Integer)
     * @return
     */
    public static OkHttpClient build(final Integer connSokectTimeout, final Integer readTimeoutMs, final Integer writeTimeoutMs, 
            final Integer maxConnPerRoute, final Integer maxConnTotal) {
        return custom(connSokectTimeout, readTimeoutMs, writeTimeoutMs, maxConnPerRoute, maxConnTotal).build();
    }
    
    /**
     * @see #custom(Integer, Integer, Integer, Integer, Integer)
     * @return
     */
    public static OkHttpClient buildDefault() {
        return custom(null, null, null, null, null).build();
    }
    
    /**
     * 初始化一个OkHttpClient.Builder对象，该过程大概需要耗时?00ms，建议使用饿汉式单例模式初始化。<br>
     * 所有参数，均可以设置为null来使用默认值，如果不为null则使用设置的特定值。
     * 
     * @param connectTimeoutMs create connection timeout
     * 
     * @param readTimeoutMs read io timeout
     *            
     * @param writeTimeoutMs write io timeout
     * 
     * @param maxConnPerRoute
     *            a maximum limit of connection on a per route basis. 默认值是maxPerRoute=5，maxTotal=64。
     * @param maxConnTotal
     *            a maximum limit of connection in total.
     * 
     * @return CloseableHttpClient
     */
    public static OkHttpClient.Builder custom(final Integer connectTimeoutMs, final Integer readTimeoutMs, final Integer writeTimeoutMs, 
            final Integer maxConnPerRoute, final Integer maxConnTotal) {
        
        int maxPerRoute;
        if (maxConnPerRoute != null && maxConnPerRoute > 0) {
            maxPerRoute = maxConnPerRoute;
        } else {
            maxPerRoute = 5;
        }
        
        int maxTotal;
        if (maxConnTotal != null && maxConnTotal > 0) {
            maxTotal = maxConnTotal;
        } else {
            maxTotal = 64;
        }
        
        int connectTimeout;
        if (connectTimeoutMs != null) {
            connectTimeout = connectTimeoutMs;
        } else {
            // 默认是10s，是比较长的、比较保守的，实际可以根据设置短一点
            connectTimeout = 10 * 1000;
        }
        
        int readTimeout;
        if (readTimeoutMs != null) {
            readTimeout = readTimeoutMs;
        } else {
            // 默认是3分钟，是比较长的、比较保守的，实际可以根据设置短一点
            readTimeout = 180 * 1000;
        }
        
        int writeTimeout;
        if (writeTimeoutMs != null) {
            writeTimeout = writeTimeoutMs;
        } else {
            writeTimeout = 60 * 1000;
        }
        
        Dispatcher dispatcher = new Dispatcher();
        dispatcher.setMaxRequestsPerHost(maxPerRoute); // 默认5
        dispatcher.setMaxRequests(maxTotal); // 默认64
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .protocols(Collections.singletonList(Protocol.HTTP_1_1))
                // 设置完成请求的超时；调用超时跨越整个调用：解析DNS、连接、写入请求、正文、服务器处理和读取响应正文。如果呼叫需要重定向或
                // 所有重试都必须在一个超时时间内完成；默认为0，则不设置超时
                .callTimeout(Duration.ZERO)
                // 建立连接（TCP 套接字）的超时时间；默认值是10S
                .connectTimeout(Duration.ofMillis(connectTimeout))
                // 发起请求到读到响应数据的超时时间,默认是10S
                .readTimeout(Duration.ofMillis(readTimeout))
                // 发起请求并被目标服务器接受的超时时间，默认是10S
                .writeTimeout(Duration.ofMillis(writeTimeout))
                // 默认连接池配置默认5,5
                .connectionPool(new ConnectionPool(5, 5, TimeUnit.MINUTES)).dispatcher(dispatcher);

        return builder;
    }
}
