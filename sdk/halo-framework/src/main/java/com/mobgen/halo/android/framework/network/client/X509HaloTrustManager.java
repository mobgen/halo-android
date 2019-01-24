package com.mobgen.halo.android.framework.network.client;

import android.support.annotation.Nullable;

import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

@SuppressWarnings("unused")
public class X509HaloTrustManager implements X509TrustManager {
    private List<X509TrustManager> trustManagers;

    public X509HaloTrustManager(List<X509TrustManager> trustManagers) {
        this.trustManagers = Collections.unmodifiableList(trustManagers);
    }

    public X509HaloTrustManager(@Nullable KeyStore keystore) {
        List<X509TrustManager> trustManagers = new ArrayList<>();
        trustManagers.add(getDefaultTrustManager());
        if(keystore != null) {
            trustManagers.add(getTrustManager(keystore));
        }

        this.trustManagers = Collections.unmodifiableList(trustManagers);
    }

    @Override
    public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        for (X509TrustManager trustManager : trustManagers) {
            try {
                trustManager.checkClientTrusted(chain, authType);
                return; // someone trusts them. success!
            } catch (CertificateException e) {
                // maybe someone else will trust them
            }
        }
        throw new CertificateException("None of the TrustManagers trust this certificate chain");
    }

    @Override
    public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        for (X509TrustManager trustManager : trustManagers) {
            try {
                trustManager.checkServerTrusted(chain, authType);
                return; // someone trusts them. success!
            } catch (CertificateException e) {
                // maybe someone else will trust them
            }
        }
        throw new CertificateException("None of the TrustManagers trust this certificate chain");
    }

    @Override
    public X509Certificate[] getAcceptedIssuers() {
        List<X509Certificate> certificates = new ArrayList<>();
        for (X509TrustManager trustManager : trustManagers) {
            for (X509Certificate cert : trustManager.getAcceptedIssuers()) {
                certificates.add(cert);
            }
        }

        return certificates.toArray(new X509Certificate[certificates.size()]);
        /*
        ImmutableList.Builder<X509Certificate> certificates = ImmutableList.builder();
        for (X509TrustManager trustManager : trustManagers) {
            for (X509Certificate cert : trustManager.getAcceptedIssuers()) {
                certificates.add(cert);
            }
        }
        return Iterables.toArray(certificates.build(), X509Certificate.class);
        */
    }

    public static TrustManager[] getTrustManagers(KeyStore keyStore) {
        return new TrustManager[] { new X509HaloTrustManager(keyStore) };
    }

    public static X509TrustManager getDefaultTrustManager() {
        return getTrustManager(null);
    }

    public static X509TrustManager getTrustManager(KeyStore keystore) {
        return getTrustManager(TrustManagerFactory.getDefaultAlgorithm(), keystore);
    }

    public static X509TrustManager getTrustManager(String algorithm, KeyStore keystore) {
        TrustManagerFactory factory;

        try {
            factory = TrustManagerFactory.getInstance(algorithm);
            factory.init(keystore);
            return (X509TrustManager) factory.getTrustManagers()[0];
            /*
            return Iterables.getFirst(Iterables.filter(
                    Arrays.asList(factory.getTrustManagers()), X509TrustManager.class), null);
                    */
        } catch (NoSuchAlgorithmException | KeyStoreException e) {
            e.printStackTrace();
        }

        return null;
    }
}