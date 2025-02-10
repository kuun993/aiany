package com.aiany.openai;

import java.net.Proxy;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import com.aiany.core.Client;
import com.aiany.core.Tokenizer;
import com.aiany.core.message.AssistantMessage;
import com.aiany.core.message.Message;
import com.aiany.core.model.StreamingChatModel;
import com.aiany.core.model.StreamingResponseHandler;
import com.aiany.core.request.ChatCompletionRequest;
import com.aiany.core.request.Tool;
import com.aiany.core.response.ChatCompletionResponse;
import com.aiany.core.response.Response;

import lombok.Builder;
import retrofit2.Call;
import retrofit2.Callback;

public class OpenAiStreamingChatModel implements StreamingChatModel {

    private final OpenAiClient openAiClient;

    private Client.Options options;

    @Builder
    public OpenAiStreamingChatModel(String baseUrl,
            String apiKey,
            String organizationId,
            String modelName,
            Double temperature,
            Duration timeout,
            Integer maxRetries,
            Proxy proxy,
            Boolean logRequests,
            Boolean logResponses,
            Tokenizer tokenizer,
            Map<String, String> customHeaders) {

        final Client.Options options = Client.Options.builder()
                .baseUrl(baseUrl)
                .apiKey(apiKey)
                .organizationId(organizationId)
                .model(modelName)
                .callTimeout(timeout)
                .connectTimeout(timeout)
                .readTimeout(timeout)
                .writeTimeout(timeout)
                .proxy(proxy)
                .logRequests(logRequests)
                .logResponses(logResponses)
                .customHeaders(customHeaders)
                .build();
        this.options = options;
        this.openAiClient = OpenAiClient.builder().options(options).build();
    }

    @Override
    public void chat(List<Message> messages, List<Tool> tools, StreamingResponseHandler<AssistantMessage> handler) {
        ChatCompletionRequest chatCompletionRequest = buildChatCompletionRequest(messages, tools);
        Call<ChatCompletionResponse> call = openAiClient.call(chatCompletionRequest);
        CountDownLatch countDownLatch = new CountDownLatch(1);
        OpenAiStreamingResponseBuilder openAiStreamingResponseBuilder = new OpenAiStreamingResponseBuilder();
        call.enqueue(new Callback<ChatCompletionResponse>() {
            @Override
            public void onResponse(Call<ChatCompletionResponse> call, retrofit2.Response<ChatCompletionResponse> response) {
                ChatCompletionResponse chatCompletionResponse = response.body();
                openAiStreamingResponseBuilder.append(chatCompletionResponse, handler);
            }

            @Override
            public void onFailure(Call<ChatCompletionResponse> call, Throwable t) {
                handler.onError(t);
                countDownLatch.countDown();
            }
            
        });
        // 等待回复完成
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        Response<AssistantMessage> response = openAiStreamingResponseBuilder.build();
        handler.onComplete(response);
    }



    /**
     * 构建请求参数
     * @param messages  消息
     * @param tools    工具
     * @return  ChatCompletionRequest
     */
    private ChatCompletionRequest buildChatCompletionRequest(List<Message> messages, List<Tool> tools) {
        return ChatCompletionRequest.builder()
                .messages(messages)
                .tools(tools)
                .model(options.model)
                .build();
    }

}
