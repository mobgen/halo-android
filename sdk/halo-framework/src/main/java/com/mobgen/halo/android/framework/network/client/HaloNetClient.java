package com.mobgen.halo.android.framework.network.client;

import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.util.Log;

import com.mobgen.halo.android.framework.R;
import com.mobgen.halo.android.framework.common.annotations.Api;
import com.mobgen.halo.android.framework.common.helpers.logger.Halog;
import com.mobgen.halo.android.framework.common.utils.AssertionUtils;
import com.mobgen.halo.android.framework.common.utils.HaloUtils;
import com.mobgen.halo.android.framework.network.client.endpoint.HaloEndpointCluster;
import com.mobgen.halo.android.framework.network.client.request.HaloRequest;
import com.mobgen.halo.android.framework.network.client.response.TypeReference;
import com.mobgen.halo.android.framework.network.exceptions.HaloNetException;
import com.mobgen.halo.android.framework.network.exceptions.HaloNetParseException;
import com.mobgen.halo.android.framework.network.exceptions.HaloNetworkExceptionResolver;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Arrays;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import okhttp3.CertificatePinner;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * The network client that wraps the okHttp client.
 */
public class HaloNetClient {

    /**
     * The application context.
     */
    protected final Context mContext;

    /**
     * The endpoint.
     */
    private final HaloEndpointCluster mEndpoints;

    /**
     * The client instance of the okHttp wrapper.
     */
    private OkHttpClient mClient;

    /**
     * Deep copy constructor.
     *
     * @param context       The application context.
     * @param clientBuilder The client to copy.
     * @param endpoints     The endpoint cluster with the different endpoints and the cert pinning.
     */
    public HaloNetClient(@NonNull Context context, @NonNull OkHttpClient.Builder clientBuilder, @NonNull HaloEndpointCluster endpoints) {
        AssertionUtils.notNull(context, "context");
        AssertionUtils.notNull(clientBuilder, "client");
        AssertionUtils.notNull(endpoints, "endpoints");
        mContext = context;
        mEndpoints = endpoints;
        mClient = buildCertificates(clientBuilder).build();
    }

    /**
     * Builds the certificate pinner for this client.
     *
     * @param okBuilder The okhttp builder.
     * @return The current builder.
     */
    @NonNull
    @Api(2.0)
    public OkHttpClient.Builder buildCertificates(@NonNull OkHttpClient.Builder okBuilder) {
        CertificatePinner pinner = mEndpoints.buildCertificatePinner();

        if (pinner != null) {
            //The certificate pinning SHA if available
            okBuilder.certificatePinner(pinner);
        }

        try {
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            InputStream caInput = mContext.getResources().openRawResource(R.raw.inverted_cert);
            Certificate ca;
            try {
                ca = cf.generateCertificate(caInput);
                System.out.println("ca=" + ((X509Certificate) ca).getSubjectDN());
            } finally {
                caInput.close();
            }

            // Create a KeyStore containing our trusted CAs
            String keyStoreType = KeyStore.getDefaultType();
            KeyStore keyStore = KeyStore.getInstance(keyStoreType);
            keyStore.load(null, null);
            keyStore.setCertificateEntry("ca", ca);

            TrustManager[] trustManagers = X509HaloTrustManager.getTrustManagers(keyStore);
            SSLSocketFactoryCompat socketFactoryCompat = new SSLSocketFactoryCompat(trustManagers);
            if(trustManagers[0] instanceof X509TrustManager) {
                okBuilder.sslSocketFactory(socketFactoryCompat, (X509TrustManager) trustManagers[0]);
            } else {
                okBuilder.sslSocketFactory(socketFactoryCompat);
            }
        } catch (CertificateException ce) {
            ce.printStackTrace();
        } catch (FileNotFoundException fe) {
            fe.printStackTrace();
        } catch (IOException io) {
            io.printStackTrace();
        } catch (KeyStoreException ke) {
            ke.printStackTrace();
        } catch (NoSuchAlgorithmException ne) {
            ne.printStackTrace();
        }

        return okBuilder;
    }

    /**
     * Performs a request based on its parameters.
     *
     * @param haloRequest The request to perform.
     * @return The result of the request.
     * @throws HaloNetException Error while performing the request.
     */
    @Api(2.0)
    public Response request(@NonNull HaloRequest haloRequest) throws HaloNetException {
        Request request = haloRequest.buildOkRequest();
        try {
            Response response = mClient.newCall(request).execute();
            if (response.isSuccessful()) {
                return response;
            } else {
                throw new HaloNetworkExceptionResolver().resolve(response);
            }
        } catch (HaloNetException e) {
            throw e;
        } catch (Exception e) {
            throw new HaloNetworkExceptionResolver().resolve(e, request, HaloUtils.isNetworkConnected(mContext));
        }
    }

    /**
     * Performs a request based on its parameters.
     *
     * @param haloRequest The request to perform.
     * @param type        The response class that will be used to cast the data.
     * @return The result of the request.
     * @throws HaloNetException Error while performing the request.
     */
    @SuppressWarnings("unchecked")
    @Api(2.0)
    public <T> T request(@NonNull HaloRequest haloRequest, @NonNull final TypeReference<T> type) throws HaloNetException {
        Response stream = request(haloRequest);
        try {
            return (T) haloRequest.getParser().deserialize(type.getType()).convert(stream.body().byteStream());
        } catch (IOException e) {
            throw new HaloNetParseException("Error parsing the stream", e);
        }
    }

    /**
     * Performs a request based on its parameters.
     *
     * @param haloRequest The request to perform.
     * @param clazz       The response class that will be used to cast the data.
     * @return The result of the request.
     * @throws HaloNetException Error while performing the request.
     */
    @SuppressWarnings("unchecked")
    @Api(2.0)
    public <T> T request(@NonNull HaloRequest haloRequest, @NonNull Class<T> clazz) throws HaloNetException {
        Response stream = request(haloRequest);
        try {
            return (T) haloRequest.getParser().deserialize(clazz).convert(stream.body().byteStream());
        } catch (IOException e) {
            throw new HaloNetParseException("Error parsing the stream", e);
        }
    }

    /**
     * Provides the endpoint.
     *
     * @return The endpoint.
     */
    @Api(2.0)
    public HaloEndpointCluster endpoints() {
        return mEndpoints;
    }

    /**
     * Provides the okhttp instance for this halo client. This allows a fine grained use of the client
     * but we discourage it unless you really need to.
     *
     * @return The client.
     */
    @NonNull
    @Api(2.0)
    public OkHttpClient ok() {
        return mClient;
    }

    /**
     * Overrides the ok http client that is in by default.
     *
     * @param builder The builder.
     */
    @Api(2.0)
    public void overrideOk(@NonNull OkHttpClient.Builder builder) {
        mClient = buildCertificates(builder).build();
    }

    /**
     * Provides the context of the http client.
     *
     * @return The context.
     */
    @NonNull
    @Api(2.0)
    public Context context() {
        return mContext;
    }

    /**
     * Close cache. This should be called just before uninstall halo.
     */
    @NonNull
    @Api(2.4)
    public void closeCache() {
        if (mClient.cache() != null && !mClient.cache().isClosed()) {
            try {
                mClient.cache().close();
            } catch (IOException e) {
                Halog.v(HaloNetClient.class, "Could not close cache");
            }
        }
    }
}
