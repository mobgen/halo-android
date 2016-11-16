package com.mobgen.halo.android.framework.network.interceptors;

import com.mobgen.halo.android.framework.common.helpers.logger.Halog;

import java.io.IOException;
import java.nio.charset.Charset;

import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.Buffer;

/**
 * OkHttp interceptor for halo.
 */
public class HaloCurlInterceptor implements Interceptor {

    /**
     * Utf-8 charset.
     */
    private static final Charset UTF8 = Charset.forName("UTF-8");

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        boolean compressed = false;

        StringBuilder builder = new StringBuilder("curl")
                .append(" -X ")
                .append(request.method());

        Headers headers = request.headers();
        for (int i = 0, count = headers.size(); i < count; i++) {
            String name = headers.name(i);
            String value = headers.value(i);
            if ("Accept-Encoding".equalsIgnoreCase(name) && "gzip".equalsIgnoreCase(value)) {
                compressed = true;
            }
            builder.append(" -H \"").append(name).append(": ").append(value).append("\"");
        }
        if(request.body() != null && request.body().contentType() != null) {
            builder.append(" -H \"").append("Content-Type").append(": ").append(request.body().contentType().toString()).append("\"");
        }

        RequestBody requestBody = request.body();
        if (requestBody != null) {
            Buffer buffer = new Buffer();
            requestBody.writeTo(buffer);
            Charset charset = UTF8;
            MediaType contentType = requestBody.contentType();
            if (contentType != null) {
                charset = contentType.charset(UTF8);
            }
            builder.append(" --data '").append(buffer.readString(charset).replace("\n", "\\n")).append("'");
        }

        builder.append(compressed ? " --compressed " : " ").append(request.url());

        Halog.d(getClass(), builder.toString());
        return chain.proceed(request);
    }

}