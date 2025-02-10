package com.aiany.openai;

import java.net.Proxy;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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
import com.google.gson.Gson;

import lombok.Builder;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSourceListener;
import okhttp3.sse.EventSources;

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
        OpenAiStreamingResponseBuilder openAiStreamingResponseBuilder = new OpenAiStreamingResponseBuilder();
        Gson gson = Client.getGson();
        Request request = new Request.Builder()
                .url(this.options.baseUrl + "/chat/completions")
                .post(RequestBody.create(MediaType.get("application/json; charset=utf-8"),
                        gson.toJson(chatCompletionRequest)))
                .build();

        EventSources.createFactory(openAiClient.getOkHttpClient()).newEventSource(request, new EventSourceListener() {

            @Override
            public void onEvent(EventSource eventSource, String id, String type, String data) {
                if (Objects.equals(data, endTag())) {
                    Response<AssistantMessage> response = openAiStreamingResponseBuilder.build();
                    handler.onComplete(response);
                    return;
                }
                ChatCompletionResponse chatCompletionResponse = gson.fromJson(data, ChatCompletionResponse.class);
                openAiStreamingResponseBuilder.append(chatCompletionResponse, handler);
            }

            @Override
            public void onFailure(EventSource eventSource, Throwable t, okhttp3.Response response) {
                handler.onFailure(t);
            }

        });

    }

    /**
     * 构建请求参数
     * 
     * @param messages 消息
     * @param tools    工具
     * @return ChatCompletionRequest
     */
    private ChatCompletionRequest buildChatCompletionRequest(List<Message> messages, List<Tool> tools) {
        return ChatCompletionRequest.builder()
                .stream(true)
                .messages(messages)
                .tools(tools)
                .model(options.model)
                .build();
    }

}
