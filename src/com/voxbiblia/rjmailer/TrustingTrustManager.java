package com.voxbiblia.rjmailer;

import javax.net.ssl.X509TrustManager;
import java.security.cert.X509Certificate;
import java.security.cert.CertificateException;

/**
 * This TrustManager has nothing but trust.
 **/
public class TrustingTrustManager implements X509TrustManager
{
    public void checkClientTrusted(X509Certificate[] x509Certificates, String s)
            throws CertificateException
    {
        // always trust
    }

    public void checkServerTrusted(X509Certificate[] x509Certificates, String s)
            throws CertificateException
    {
        // always trust
    }

    public X509Certificate[] getAcceptedIssuers()
    {
        // we know nothing
        return new X509Certificate[0];
    }
}