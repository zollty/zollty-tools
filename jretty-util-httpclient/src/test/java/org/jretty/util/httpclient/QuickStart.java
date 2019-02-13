package org.jretty.util.httpclient;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.HeaderIterator;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.AbstractResponseHandler;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.jretty.log.LogFactory;
import org.jretty.log.Logger;
import org.jretty.util.Const;
import org.jretty.util.HttpClientStaticBuilder;
import org.jretty.util.IOUtils;

public class QuickStart {
    
    private static final Logger LOG = LogFactory.getLogger();
    
    
    /**
     * 成功则返回空，失败则报错（throws an {@link HttpResponseException}）
     * 
     * @author zollty
     */
    static public class ResponseVoidHandler extends AbstractResponseHandler<Void> {
        @Override
        public Void handleEntity(final HttpEntity entity) throws IOException {
            return null;
        }
    }
    
    /**
     * 成功则返回空，失败则返回 {@link StatusLine}，
     * 可以获取statusLine.getStatusCode(), statusLine.getReasonPhrase()
     * 
     * @author zollty
     */
    static public class ResponseFailHandler implements ResponseHandler<StatusLine> {
        /**
         * Read the entity from the response body and pass it to the entity handler
         * method if the response was successful (a 2xx status code). If no response
         * body exists, this returns null. If the response was unsuccessful (&gt;= 300
         * status code), return an {@link StatusLine}.
         */
        @Override
        public StatusLine handleResponse(final HttpResponse response)
                throws HttpResponseException, IOException {
            if(LOG.isTraceEnabled()) {
                printResponseInfo(response);
            }
            final StatusLine statusLine = response.getStatusLine();
            final HttpEntity entity = response.getEntity();
            if (statusLine.getStatusCode() >= 300) {
                EntityUtils.consume(entity);
                return statusLine;
            }
            return null;
        }
    }
    
    
    static void responseVoidHandler(final CloseableHttpClient httpclient, final HttpUriRequest request) {
        long stime = System.currentTimeMillis();
        try {
            httpclient.execute(request, new ResponseVoidHandler());
            
            // 成功
            LOG.info("request '{}' success, cost time {}.", request.getURI(), System.currentTimeMillis() - stime);

        } catch (IOException e) {
            // 失败
            LOG.error(e, "request '{}' fail, cost time {}.", request.getURI(), System.currentTimeMillis() - stime);
        }
    }
    
    static void responseStringHandler(final CloseableHttpClient httpclient, final HttpUriRequest request) {
        long stime = System.currentTimeMillis();
        try {
            String responseString = httpclient.execute(request, new BasicResponseHandler());

            // 成功
            LOG.info("request '{}' success, cost time {}. response body: {}", 
                    request.getURI(), System.currentTimeMillis() - stime, responseString);

        } catch (IOException e) {
            // 失败
            LOG.error(e, "request '{}' fail, cost time {}.", request.getURI(), System.currentTimeMillis() - stime);
        }
    }
    
    static void responseFailHandler(final CloseableHttpClient httpclient, final HttpUriRequest request) {
        long stime = System.currentTimeMillis();
        try {
            StatusLine statusLine = httpclient.execute(request, new ResponseFailHandler());
            
            if(statusLine==null){
                // 成功
                LOG.info("request '{}' success, cost time {}.", request.getURI(), System.currentTimeMillis() - stime);
            } else {
                // 失败
                LOG.info("request '{}' fail, cost time {}. StatusLine: {}", 
                        request.getURI(), System.currentTimeMillis() - stime, statusLine);
            }

        } catch (IOException e) {
            // 失败
            LOG.error(e, "request '{}' fail, cost time {}.", request.getURI(), System.currentTimeMillis() - stime);
        }
    }
    
    static HttpPost buildHttpPost() {
        HttpPost httpPost = new HttpPost("http://httpbin.org/post");
        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        nvps.add(new BasicNameValuePair("username", "vip"));
        nvps.add(new BasicNameValuePair("password", "secret"));
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(nvps));
        } catch (UnsupportedEncodingException e) {
            LOG.error(e);
        }
        
        return httpPost;
    }
    
    
    public static void main(String[] args) {
        CloseableHttpClient httpclient = HttpClientStaticBuilder.build(1000, 3000, null, 2, 20);
        final String url = "http://httpbin.org/get";
        HttpGet httpGet = new HttpGet("http://172.19.15.54:8089/cst-ici-datasync/sysCarData/save");
        responseStringHandler(httpclient, httpGet);
        
        responseVoidHandler(httpclient, httpGet);
        
        responseFailHandler(httpclient, httpGet);
        
        HttpPost httpPost = buildHttpPost();
        
        responseStringHandler(httpclient, httpPost);
        
        responseVoidHandler(httpclient, httpPost);
        
        responseFailHandler(httpclient, httpPost);
        
        IOUtils.close(httpclient);
    }
    
    
    static HttpGet buildGet(final String url, final Map<String, Object> paramsMap) {
        HttpGet httpGet = new HttpGet(invokeUrl(url, paramsMap));
        return httpGet;
    }
    /** 
     * GET方式传参 
     */  
    public static String invokeUrl(final String url, final Map<String, Object> paramsMap) {
        if (paramsMap == null || paramsMap.isEmpty()) {
            return url;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(url);
        int i = 0;
        for (Map.Entry<String, Object> entry : paramsMap.entrySet()) {
            if (i == 0 && !url.contains("?")) {
                sb.append("?");
            } else {
                sb.append("&");
            }
            sb.append(entry.getKey());
            sb.append("=");
            Object value = entry.getValue();
            try {
                sb.append(URLEncoder.encode(value.toString(), Const.UTF_8));
            } catch (UnsupportedEncodingException e) {
                LOG.warn("encode http get params error, value is {}, reason: ", value, e.toString());
                try {
                    sb.append(URLEncoder.encode(value.toString(), null));
                } catch (UnsupportedEncodingException e1) {
                    LOG.error(e1);
                }
            }
            i++;
        }
        return sb.toString();
    }
    
    /** 
     * 将传入的键/值对参数转换为NameValuePair参数集 
     *  
     * @param paramsMap 参数集, 键/值对 
     * @return NameValuePair参数集 
     */  
    public static List<NameValuePair> getParamsList(Map<String, Object> paramsMap) {
        if (paramsMap == null || paramsMap.isEmpty()) {
            return null;
        }
        // 创建参数队列
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        for (Map.Entry<String, Object> map : paramsMap.entrySet()) {
            params.add(new BasicNameValuePair(map.getKey(), map.getValue().toString()));
        }
        return params;
    }
    
    public static void printResponseInfo(HttpResponse resp) {
        HttpEntity entity = resp.getEntity();
        if (entity != null) {
            long responseLength = entity.getContentLength();
            System.err.println("内容编码: " + entity.getContentEncoding());
            System.err.println("响应状态: " + resp.getStatusLine());
            System.err.println("响应长度: " + responseLength);
        }
        printHeaders(resp);
    }
    
    
    // 打印头信息  
    public static void printHeaders(HttpResponse httpResponse) {
        System.err.println("Header start------------------------------");
        // 头信息
        HeaderIterator it = httpResponse.headerIterator();
        while (it.hasNext()) {
            System.err.println(it.next());
        }
        System.err.println("Header end-------------------------------");
    }

}