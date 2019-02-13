package org.jretty.util;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.CloseableHttpPipeliningClient;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.impl.nio.conn.PoolingNHttpClientConnectionManager;
import org.apache.http.impl.nio.reactor.DefaultConnectingIOReactor;
import org.apache.http.impl.nio.reactor.IOReactorConfig;
import org.apache.http.nio.reactor.ConnectingIOReactor;
import org.apache.http.nio.reactor.IOReactorException;
import org.jretty.util.NestedRuntimeException;

/**
 * Create an HttpClient with the given custom dependencies and configuration. 参见build方法
 * @see #build(Integer, Integer, Integer, Integer, Integer)
 *  
 * @author zollty
 * @since 2017-1-10
 */
public class HttpAsynClientStaticBuilder {
    /*
    with spring, you can use:
     <bean id="httpClient" class="org.jrrety.util.HttpClientStaticBuilder"
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
    public static CloseableHttpAsyncClient build(final Integer connRequestTimeout, final Integer connSokectTimeout, final Integer soTimeout, 
            final Integer maxConnPerRoute, final Integer maxConnTotal) {
        return custom(connRequestTimeout, connSokectTimeout, soTimeout, maxConnPerRoute, maxConnTotal).build();
    }
    
    /**
     * @see #custom(Integer, Integer, Integer, Integer, Integer)
     * @return
     */
    public static CloseableHttpAsyncClient buildDefault() {
        return custom(null, null, null, null, null).build();
    }
    
    /**
     * 初始化一个CloseableHttpClient对象，该过程大概需要耗时600ms，建议使用饿汉式单例模式初始化。<br>
     * 所有参数，均可以设置为null来使用默认值，如果不为null则使用设置的特定值。
     * 
     * @param connRequestTimeout
     *            Determines the timeout in milliseconds until a connection is established. A timeout value of zero is interpreted as an infinite
     *            timeout. 默认是1分钟，是比较长的、比较保守的，实际可以根据设置短一点
     * 
     * @param connSokectTimeout
     *            Determines the timeout in milliseconds until a socket connection is established. A timeout value of zero is interpreted as an
     *            infinite timeout. 默认是1分钟，是比较长的、比较保守的，实际可以根据设置短一点
     * @see java.net.Socket#connect(java.net.SocketAddress, int)
     * 
     * @param soTimeout
     *            Socket SO_TIMEOUT with the specified timeout, in milliseconds. 默认是0（没有超时限制），建议设置一个值，没有特殊需要可以设置成180*1000
     * @see java.net.SocketOptions#SO_TIMEOUT
     * 
     * @param maxConnPerRoute
     *            a maximum limit of connection on a per route basis. 默认值是maxPerRoute=2，maxTotal=20。如果并发IP数超过20个可以调整这个参数。
     * @param maxConnTotal
     *            a maximum limit of connection in total.
     * 
     * @return CloseableHttpClient
     */
    public static HttpAsyncClientBuilder custom(final Integer connRequestTimeout, final Integer connSokectTimeout, final Integer soTimeout, 
            final Integer maxConnPerRoute, final Integer maxConnTotal) {
        
        int socketTimeout;
        if (soTimeout != null) {
            socketTimeout = soTimeout;
        } else {
            socketTimeout = 180 * 1000;
        }
        
        int maxPerRoute;
        if (maxConnPerRoute != null && maxConnPerRoute > 0) {
            maxPerRoute = maxConnPerRoute;
        } else {
            maxPerRoute = 2;
        }
        
        int maxTotal;
        if (maxConnTotal != null && maxConnTotal > 0) {
            maxTotal = maxConnTotal;
        } else {
            maxTotal = 20;
        }
        
        int connectionRequestTimeout;
        if (connRequestTimeout != null) {
            connectionRequestTimeout = connRequestTimeout;
        } else {
            // 默认是1分钟，是比较长的、比较保守的，实际可以根据设置短一点
            connectionRequestTimeout = 60 * 1000;
        }
        
        int connectTimeout;
        if (connSokectTimeout != null) {
            connectTimeout = connSokectTimeout;
        } else {
            // 默认是1分钟，是比较长的、比较保守的，实际可以根据设置短一点
            connectTimeout = 60 * 1000;
        }
        
        // Create global I/O reactor configuration
        IOReactorConfig defaultIOReactorConfig = IOReactorConfig.custom()
                .setSoTimeout(socketTimeout)
                .setConnectTimeout(connectTimeout)
                .build();
        
        // Create global request configuration，具体的每个请求都可以重新设置RequestConfig
        RequestConfig defaultRequestConfig = RequestConfig.custom()
            .setConnectionRequestTimeout(connectionRequestTimeout)
            .setConnectTimeout(connectTimeout)
            .setSocketTimeout(socketTimeout)
            .build();
        
        // Create an HttpClient with the given custom dependencies and configuration.
        HttpAsyncClientBuilder builder = HttpAsyncClientBuilder.create()
            .disableCookieManagement()
            .setDefaultIOReactorConfig(defaultIOReactorConfig)
            .setMaxConnPerRoute(maxPerRoute)
            .setMaxConnTotal(maxTotal)
            .setDefaultRequestConfig(defaultRequestConfig);

        return builder;
    }

    
    /**
     * @see #custom(Integer, Integer, Integer, Integer, Integer)
     * 
     * @return CloseableHttpPipeliningClient
     */
    public static CloseableHttpPipeliningClient buildPipeliningDefault() {
        return buildPipelining(null, null, null, null);
    }
    
    /**
     * @see #custom(Integer, Integer, Integer, Integer, Integer)
     * 
     * @return CloseableHttpPipeliningClient
     */
    public static CloseableHttpPipeliningClient buildPipelining(final Integer connSokectTimeout, final Integer soTimeout, 
            final Integer maxConnPerRoute, final Integer maxConnTotal) {
        
        int socketTimeout;
        if (soTimeout != null) {
            socketTimeout = soTimeout;
        } else {
            socketTimeout = 180 * 1000;
        }
        
        int maxPerRoute;
        if (maxConnPerRoute != null && maxConnPerRoute > 0) {
            maxPerRoute = maxConnPerRoute;
        } else {
            maxPerRoute = 2;
        }
        
        int maxTotal;
        if (maxConnTotal != null && maxConnTotal > 0) {
            maxTotal = maxConnTotal;
        } else {
            maxTotal = 20;
        }
        
        int connectTimeout;
        if (connSokectTimeout != null) {
            connectTimeout = connSokectTimeout;
        } else {
            // 默认是1分钟，是比较长的、比较保守的，实际可以根据设置短一点
            connectTimeout = 60 * 1000;
        }
        
        // Create global I/O reactor configuration
        IOReactorConfig defaultIOReactorConfig = IOReactorConfig.custom()
                .setSoTimeout(socketTimeout)
                .setConnectTimeout(connectTimeout)
                .build();
        
        ConnectingIOReactor ioreactor;
        try {
            ioreactor = new DefaultConnectingIOReactor(defaultIOReactorConfig);
        } catch (IOReactorException e) {
            throw new NestedRuntimeException(e);
        }
        final PoolingNHttpClientConnectionManager connManager = new PoolingNHttpClientConnectionManager(ioreactor);
        connManager.setMaxTotal(maxTotal);
        connManager.setDefaultMaxPerRoute(maxPerRoute);
        
        // Create an HttpClient with the given custom dependencies and configuration.
        return HttpAsyncClients.createPipelining(connManager);
    }
}