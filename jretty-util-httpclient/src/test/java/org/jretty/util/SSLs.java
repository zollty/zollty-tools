package org.jretty.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.nio.conn.ssl.SSLIOSessionStrategy;
import org.apache.http.ssl.SSLContexts;

/**
 * 设置ssl
 * 
 * @author arron
 * @date 2015年11月3日 下午3:11:54
 * @version 1.0
 */
public class SSLs {

    // 绕过证书验证，处理https请求
    private static final SSLManager ignoreVerifySSLManager = new SSLManager();
	private static SSLSocketFactory sslFactory ;
	private static SSLConnectionSocketFactory sslConnFactory ;
	private static SSLIOSessionStrategy sslIOSessionStrategy ;
	private static SSLs sslutil = new SSLs();
	private SSLContext sc;
	
	public static SSLs getInstance(){
		return sslutil;
	}
	public static SSLs custom(){
		return new SSLs();
	}

    // 重写X509TrustManager类的三个方法,信任服务器证书
    private static class SSLManager implements  X509TrustManager, HostnameVerifier{
		
		@Override
		public java.security.cert.X509Certificate[] getAcceptedIssuers() {
			return new java.security.cert.X509Certificate[]{};
			//return null;
		}
		
		@Override
		public void checkServerTrusted(java.security.cert.X509Certificate[] chain,
				String authType) throws java.security.cert.CertificateException {
		}
		
		@Override
		public void checkClientTrusted(java.security.cert.X509Certificate[] chain,
				String authType) throws java.security.cert.CertificateException {
		}

		@Override
		public boolean verify(String paramString, SSLSession paramSSLSession) {
			return true;
		}
	};
    
	// 信任主机
    public static HostnameVerifier getVerifier() {
        return ignoreVerifySSLManager;
    }
    
    public synchronized SSLSocketFactory getSSLSF() throws KeyManagementException,NoSuchAlgorithmException {
        if (sslFactory != null)
            return sslFactory;
        SSLContext sc = getSSLContext();
        initSSLContext(sc, null);
        sslFactory = sc.getSocketFactory();
        return sslFactory;
    }
    
    public synchronized SSLConnectionSocketFactory getSSLCONNSF() throws KeyManagementException, NoSuchAlgorithmException {
    	if (sslConnFactory != null)
    		return sslConnFactory;
    	SSLContext sc = getSSLContext();
    	initSSLContext(sc, new java.security.SecureRandom());
        sslConnFactory = new SSLConnectionSocketFactory(sc, ignoreVerifySSLManager);
    	return sslConnFactory;
    }
    
    public synchronized SSLIOSessionStrategy getSSLIOSS() throws KeyManagementException, NoSuchAlgorithmException {
    	if (sslIOSessionStrategy != null)
    		return sslIOSessionStrategy;
        SSLContext sc = getSSLContext();
        initSSLContext(sc, new java.security.SecureRandom());
        sslIOSessionStrategy = new SSLIOSessionStrategy(sc, ignoreVerifySSLManager);
    	return sslIOSessionStrategy;
    }
    
    public SSLContext getSSLContext() throws NoSuchAlgorithmException {
        if(sc==null){
            sc = SSLContext.getInstance("SSLv3");
        }
        return sc;
    }
    
    private void initSSLContext(SSLContext sc,  SecureRandom random) throws KeyManagementException {
//      sc.init(null, new TrustManager[] { ignoreVerifySSLManager }, null);
        sc.init(null, new TrustManager[] { ignoreVerifySSLManager }, random);
    }
    
    /**
     * 设置信任自定义的证书
     * 
     * @param keyStorePath      密钥库路径
     * @param keyStorepass      密钥库密码  "nopassword"
     * @return
     **/
    public SSLs customSSL(String keyStorePath, String keyStorepass) throws GeneralSecurityException, IOException {
    	FileInputStream instream =null;
    	KeyStore trustStore = null; 
		try {
		    trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            instream = new FileInputStream(new File(keyStorePath));
            trustStore.load(instream, keyStorepass.toCharArray());
            // 相信自己的CA和所有自签名的证书
            sc= SSLContexts.custom().loadTrustMaterial(trustStore, new TrustSelfSignedStrategy()) .build();
		} finally{
			try {
				instream.close();
			} catch (IOException e) {}
		}
		return this;
    }
    
}