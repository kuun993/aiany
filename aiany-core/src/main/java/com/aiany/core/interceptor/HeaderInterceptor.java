package com.aiany.core.interceptor;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.Map;

/**
 * @author waani
 */
public class HeaderInterceptor implements Interceptor {

    /**
     * 自定义请求头
     */
    private final Map<String, String> header;

    public HeaderInterceptor(Map<String, String> header) {
        this.header = header;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        if (header != null) {
            final Request.Builder builder = request.newBuilder();
            header.forEach(builder::addHeader);
            request = builder.build();
        }
        return chain.proceed(request);
    }
}
