/**
 * 
 */
package org.jretty.util.httpclient;

import java.io.IOException;

import org.apache.http.HttpHost;
import org.apache.http.client.fluent.Executor;
import org.apache.http.client.fluent.Request;
import org.jretty.util.StringUtils;

/**
 * 
 * @author zollty
 * @since 2018年5月9日
 */
public class FluentApiDemo2 {
    static final String BASE_URL = "http://localhost:8810/remove?version=";

    static Executor executor = Executor.newInstance();
    public static void main(String[] args) throws IOException {

        int n = 1000;
        while(n-->0) {
            executor.execute(Request.Get(BASE_URL+n)
                    .connectTimeout(5000)
                    .socketTimeout(5000)
                    );
            System.out.println(n);
        }
       
    }

    

}
