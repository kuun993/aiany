package com.aiany.openai;

import com.aiany.core.Client;
import com.aiany.core.api.ChatApi;
import com.aiany.core.exception.AiAnyException;
import com.aiany.core.interceptor.HeaderInterceptor;
import com.aiany.core.request.ChatCompletionRequest;
import com.aiany.core.response.ChatCompletionResponse;
import lombok.Builder;
import lombok.NonNull;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * @author waani
 */
public class OpenAiClient extends Client {

    private final ChatApi openAiApi;

    @Builder
    public OpenAiClient(@NonNull Client.Options options) {
        this.openAiApi = openAiClient(options);
    }


    /**
     * 初始化 OkHttpClient
     *
     * @param options 参数配置
     * @return OkHttpClient
     */
    private OkHttpClient okHttpClient(Client.Options options) {
        Map<String, String> header = new HashMap<>();
        header.put("Authorization", "Bearer " + options.apiKey);
        HeaderInterceptor openAiInterceptor = new HeaderInterceptor(header);
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        return new OkHttpClient.Builder()
                .connectTimeout(options.connectTimeout)
                .writeTimeout(options.writeTimeout)
                .readTimeout(options.readTimeout)
                .addInterceptor(httpLoggingInterceptor)
                .addInterceptor(openAiInterceptor)
                .build();
    }


    /**
     * 初始化 OpenAiApi
     *
     * @param options 参数配置
     * @return OpenAiApi
     */
    private ChatApi openAiClient(Client.Options options) {
        return new Retrofit.Builder()
                .baseUrl(options.baseUrl)
                .client(okHttpClient(options))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(converterFactory())
                .build().create(ChatApi.class);
    }

    private Converter.Factory converterFactory() {
        return GsonConverterFactory.create(getGson());
    }


    @Override
    public ChatCompletionResponse chatCompletions(ChatCompletionRequest chatCompletionRequest) {
        final Call<ChatCompletionResponse> chatCompletionResponseCall = openAiApi.chatCompletions(chatCompletionRequest);
        try {
            return chatCompletionResponseCall.execute().body();
        } catch (Exception e) {
            throw new AiAnyException(e);
        }
    }
}
