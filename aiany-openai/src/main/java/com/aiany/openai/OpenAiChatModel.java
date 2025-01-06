package com.aiany.openai;

import com.aiany.core.Client;
import com.aiany.core.Tokenizer;
import com.aiany.core.message.AssistantMessage;
import com.aiany.core.message.Message;
import com.aiany.core.message.UserMessage;
import com.aiany.core.model.ChatModel;
import com.aiany.core.request.ChatCompletionRequest;
import com.aiany.core.response.ChatCompletionResponse;
import com.aiany.core.response.Response;
import lombok.Builder;
import java.net.Proxy;
import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author waani
 */
public class OpenAiChatModel implements ChatModel {

    private final OpenAiClient openAiClient;

    @Builder
    public OpenAiChatModel(String baseUrl,
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
                .callTimeout(timeout)
                .connectTimeout(timeout)
                .readTimeout(timeout)
                .writeTimeout(timeout)
                .proxy(proxy)
                .logRequests(logRequests)
                .logResponses(logResponses)
                .customHeaders(customHeaders)
                .build();
        this.openAiClient = OpenAiClient.builder().options(options).build();
    }

    @Override
    public String chat(String prompt) {
        UserMessage userMessage = UserMessage.create(prompt);
        final ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest.builder()
                .messages(Collections.singletonList(userMessage))
                .build();
        final ChatCompletionResponse chatCompletionResponse = openAiClient.chatCompletions(chatCompletionRequest);
        return chatCompletionResponse.getResult();
    }

    @Override
    public Response<AssistantMessage> chat(UserMessage userMessage) {
        return chat(Collections.singletonList(userMessage));
    }

    @Override
    public Response<AssistantMessage> chat(List<Message> messages) {
        final ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest.builder()
                .messages(messages)
                .build();
        final ChatCompletionResponse chatCompletionResponse = openAiClient.chatCompletions(chatCompletionRequest);
        return toResponse(chatCompletionResponse);
    }
}