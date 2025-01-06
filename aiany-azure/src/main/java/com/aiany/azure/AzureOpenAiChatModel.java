package com.aiany.azure;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.aiany.core.Tokenizer;
import com.aiany.core.message.AssistantMessage;
import com.aiany.core.message.Message;
import com.aiany.core.message.SystemMessage;
import com.aiany.core.message.ToolResultMessage;
import com.aiany.core.message.UserMessage;
import com.aiany.core.message.tool.ToolCall;
import com.aiany.core.model.ChatModel;
import com.aiany.core.response.Response;
import com.aiany.core.utils.DefaultUtil;
import com.azure.ai.openai.OpenAIClient;
import com.azure.ai.openai.OpenAIClientBuilder;
import com.azure.ai.openai.OpenAIServiceVersion;
import com.azure.ai.openai.models.ChatCompletions;
import com.azure.ai.openai.models.ChatCompletionsFunctionToolCall;
import com.azure.ai.openai.models.ChatCompletionsOptions;
import com.azure.ai.openai.models.ChatCompletionsToolCall;
import com.azure.ai.openai.models.ChatRequestAssistantMessage;
import com.azure.ai.openai.models.ChatRequestMessage;
import com.azure.ai.openai.models.ChatRequestSystemMessage;
import com.azure.ai.openai.models.ChatRequestToolMessage;
import com.azure.ai.openai.models.ChatRequestUserMessage;
import com.azure.ai.openai.models.FunctionCall;
import com.azure.core.http.HttpClient;
import com.azure.core.http.ProxyOptions;
import com.azure.core.http.netty.NettyAsyncHttpClientProvider;
import com.azure.core.http.policy.ExponentialBackoffOptions;
import com.azure.core.http.policy.HttpLogDetailLevel;
import com.azure.core.http.policy.HttpLogOptions;
import com.azure.core.http.policy.RetryOptions;
import com.azure.core.util.HttpClientOptions;

public class AzureOpenAiChatModel implements ChatModel {

    private final OpenAIClient openAIClient;
    private final String deploymentName;
    private final Integer maxTokens;
    private final Double temperature;

    public AzureOpenAiChatModel(String deploymentName,
            String endpoint,
            OpenAIServiceVersion apiVersion,
            ProxyOptions proxyOptions,
            Duration timeout,
            Integer maxRetries,
            Integer maxTokens,
            Double temperature) {

        this.deploymentName = deploymentName;
        this.maxTokens = maxTokens;
        this.temperature = temperature;

        this.openAIClient = getOpenAIClientBuilder(endpoint, apiVersion, proxyOptions, timeout, maxRetries)
                .buildClient();
    }

    private OpenAIClientBuilder getOpenAIClientBuilder(String endpoint, OpenAIServiceVersion apiVersion,
            ProxyOptions proxyOptions, Duration timeout, Integer maxRetries) {

        // 默认60秒
        timeout = DefaultUtil.getOrDefault(timeout, Duration.ofSeconds(60));
        HttpClientOptions httpClientOptions = new HttpClientOptions();
        httpClientOptions.setConnectTimeout(timeout);
        httpClientOptions.setResponseTimeout(timeout);
        httpClientOptions.setReadTimeout(timeout);
        httpClientOptions.setWriteTimeout(timeout);
        httpClientOptions.setProxyOptions(proxyOptions);
        HttpClient httpClient = new NettyAsyncHttpClientProvider().createInstance(httpClientOptions);
        HttpLogOptions httpLogOptions = new HttpLogOptions();
        httpLogOptions.setLogLevel(HttpLogDetailLevel.BASIC);

        // 默认3次
        maxRetries = DefaultUtil.getOrDefault(maxRetries, 3);
        ExponentialBackoffOptions exponentialBackoffOptions = new ExponentialBackoffOptions();
        exponentialBackoffOptions.setMaxRetries(maxRetries);
        RetryOptions retryOptions = new RetryOptions(exponentialBackoffOptions);

        OpenAIClientBuilder openAIClientBuilder = new OpenAIClientBuilder()
                .endpoint(endpoint)
                .serviceVersion(apiVersion)
                .httpClient(httpClient)
                .httpLogOptions(httpLogOptions)
                .retryOptions(retryOptions);

        return openAIClientBuilder;
    }

    private ChatCompletionsOptions getChatCompletionsOptions(List<ChatRequestMessage> messages) {
        ChatCompletionsOptions options = new ChatCompletionsOptions(messages)
                .setModel(deploymentName)
                .setMaxTokens(maxTokens)
                .setTemperature(temperature);
        return options;
    }


    private List<ChatRequestMessage> getChatRequestMessage(List<Message> messages) {
        if (messages == null || messages.isEmpty()) {
            return Collections.emptyList();
        }
        List<ChatRequestMessage> chatRequestMessages = new ArrayList<>(messages.size());
        messages.forEach(m -> {
            if (m instanceof SystemMessage) {
                chatRequestMessages.add(new ChatRequestSystemMessage(m.getContent()));
            } else if (m instanceof UserMessage) {
                chatRequestMessages.add(new ChatRequestUserMessage(m.getContent()));
            } else if (m instanceof ToolResultMessage) {
                ToolResultMessage toolResultMessage = (ToolResultMessage) m;
                chatRequestMessages.add(new ChatRequestToolMessage(toolResultMessage.getContent(), toolResultMessage.getId()));
            } else if (m instanceof AssistantMessage) {
                AssistantMessage assistantMessage = (AssistantMessage) m;
                
                ChatRequestAssistantMessage chatRequestAssistantMessage = new ChatRequestAssistantMessage(assistantMessage.getContent());
                chatRequestAssistantMessage.setToolCalls(getChatCompletionsToolCalls(assistantMessage.getToolCalls()));
                chatRequestMessages.add(chatRequestAssistantMessage);
            }
        });
        return chatRequestMessages;
    }



    private List<ChatCompletionsToolCall> getChatCompletionsToolCalls(List<ToolCall> toolCalls) {
        if (toolCalls == null || toolCalls.isEmpty()) {
            return Collections.emptyList();
        }
        List<ChatCompletionsToolCall> chatCompletionsToolCalls = new ArrayList<>(toolCalls.size());
        toolCalls.forEach(tc -> {
            ChatCompletionsFunctionToolCall chatCompletionsFunctionToolCall = 
            new ChatCompletionsFunctionToolCall(tc.getId(), new FunctionCall(tc.getName(), tc.getArguments()));
            chatCompletionsToolCalls.add(chatCompletionsFunctionToolCall);
        });
        return chatCompletionsToolCalls;
    }


    @Override
    public String chat(String prompt) {

        return null;
    }

    @Override
    public Response<AssistantMessage> chat(UserMessage userMessage) {
        return null;
    }

    @Override
    public Response<AssistantMessage> chat(List<Message> messages) {
        ChatCompletions chatCompletions = openAIClient.getChatCompletions(deploymentName, options);
        return null;
    }

}
