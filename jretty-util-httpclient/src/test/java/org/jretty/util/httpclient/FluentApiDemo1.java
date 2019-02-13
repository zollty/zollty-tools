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
public class FluentApiDemo1 {
    static final String BASE_URL = "http://192.2.192.22:8081/nexus/service/local/repositories/snapshots/content/";
    static final String CTNT_URL = "http://192.2.192.22:8081/nexus/content/repositories/snapshots/com/zollty/";
    static void test1() throws IOException {
        String ret = 
        Request.Get("http://192.2.192.22:8081/nexus/content/repositories/snapshots/com/zollty/fastdfs-client/1.0.0-SNAPSHOT/")
        .connectTimeout(5000)
        .socketTimeout(5000)
        .execute().returnContent().asString();
        
        //System.out.println(ret);
        
        Executor executor = Executor.newInstance()
//                .auth(new HttpHost("somehost"), "username", "password")
                .auth(new HttpHost("192.2.192.22", 8081), "admin", "admin123");
//                .authPreemptive(new HttpHost("myproxy", 8080));
        String url = BASE_URL + "com/zollty/fast-base/1.0.3-SNAPSHOT/fast-base-1.0.3-20180503.101732-1-sources.jar";
        System.out.println(executor.execute(Request.Delete(url)).returnContent());
        
        ret = 
//        executor.execute(Request.Delete(url))
          executor.execute(Request.Get("http://192.2.192.22:8081/nexus/service/local/repositories/snapshots/content/com/zollty/fast-base/1.0.3-SNAPSHOT/?isLocal&_dc=1525836524824"))
                .returnContent().asString();

        System.out.println(ret);
    }
    
    static String toDelete[] = new String[] {
            
    }; 

//    static final String STA = "<a href=\""+CTNT_URL;
    static Executor executor = Executor.newInstance()
            .auth(new HttpHost("192.2.192.22", 8081), "admin", "admin123");
    static final String END = "\">";
    public static void main(String[] args) throws IOException {
        //<a href="http://192.2.192.22:8081/nexus/content/repositories/snapshots/com/zollty/fast-demo-web-archetype/">
        String ret = 
//                Request.Get(CTNT_URL)
//                .connectTimeout(5000)
//                .socketTimeout(5000)
//                .execute().returnContent().asString();
        executor.execute(Request.Get(CTNT_URL)).returnContent().asString();
       
       String STA = "<a href=\""+CTNT_URL+"fast";
       System.out.println(ret);
       System.out.println(StringUtils.afterIndex(ret, STA));
       //System.out.println(middleOfIndex(ret, STA, END));
       
       String org = ret;
       while((ret=middleOfIndex(ret, STA, END))!=null) {
           System.out.println(CTNT_URL + "fast" + ret);
           handle("fast" + ret);
           ret = StringUtils.afterIndex(org, STA + ret + END);
       }
       
    }
    
    public static void handle(String component) throws IOException {
        String ret = 
//                Request.Get(CTNT_URL+component)
//                .connectTimeout(5000)
//                .socketTimeout(5000)
//                .execute().returnContent().asString();
       executor.execute(Request.Get(CTNT_URL+component)).returnContent().asString();
       
       //System.out.println(ret);
       String STA = "<a href=\""+CTNT_URL+component;
       
       String org = ret;
       while((ret=middleOfIndex(ret, STA, END))!=null) {
           if(ret.charAt(ret.length()-1)=='/') {
               handle(component + ret);
           } else {
               if(ret.endsWith(".jar") || ret.endsWith(".pom")) {
                   
                   int idx = ret.indexOf("20180");
                   String ns = ret.substring(idx+5, idx+8);
                   //System.out.println(ns);
//                    if (Integer.valueOf(ns) < 405) {
//                        if (ret.indexOf("fast-component-") != -1 
//                                || ret.indexOf("fastdfs-spring-boot-starter-") != -1
//                                || ret.indexOf("fast-generator-") != -1) {
//                            System.err.println("dont delete " + component + ret);
//                        } else {
//                            System.out.println(CTNT_URL + component + ret);
//                            System.out.println(executor.execute(Request.Delete(CTNT_URL + component + ret)).returnContent());
//                        }
//                    }
                   System.out.println(CTNT_URL + component + ret);
                   
               }
           }
           ret = StringUtils.afterIndex(org, STA + ret + END);
       }
    }
    
    public static String middleOfIndex(String str, String index1, String index2) {
        int idx1 = str.indexOf(index1);
        if (idx1 == -1) {
            return null;
        }
        str = str.substring(idx1 + index1.length(), str.length());
        int idx2 = str.indexOf(index2);
        if (idx2 == -1) {
            return null;
        }
        return str.substring(0, idx2);
    }
    

}
