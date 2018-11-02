package cn.rsd.util;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

/**
 * @author 焦云亮
 * @date 2018/8/28
 * @modifyUser
 * @modifyDate
 */
public class MyX509TrustManager {

    public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
    }

    public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
    }

    public X509Certificate[] getAcceptedIssuers() {
        return null;
    }
}
