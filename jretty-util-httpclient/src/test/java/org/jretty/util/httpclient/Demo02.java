package org.jretty.util.httpclient;

/**
 * 单线程，频繁往多个IP发送请求。
 * 
 * 同 Demo01
 * 
 * @see Readme.java
 * 
 * @author zollty
 * @since 2017-1-13
 */
public class Demo02 {
    
    public static void main(String[] args) throws Exception {
        
        for(int i=0; i<20; i++) {
            Demo01.post();
        }
    }


}
