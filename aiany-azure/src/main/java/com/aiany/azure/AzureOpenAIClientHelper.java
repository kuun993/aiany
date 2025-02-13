package com.aiany.azure;

import com.aiany.core.message.*;
import com.aiany.core.message.tool.ToolCall;
import com.aiany.core.request.Function;
import com.aiany.core.request.Parameters;
import com.aiany.core.request.Tool;
import com.aiany.core.utils.DefaultUtil;
import com.azure.ai.openai.OpenAIClientBuilder;
import com.azure.ai.openai.OpenAIServiceVersion;
import com.azure.ai.openai.models.*;
import com.azure.core.credential.AzureKeyCredential;
import com.azure.core.http.HttpClient;
import com.azure.core.http.ProxyOptions;
import com.azure.core.http.netty.NettyAsyncHttpClientProvider;
import com.azure.core.http.policy.ExponentialBackoffOptions;
import com.azure.core.http.policy.HttpLogDetailLevel;
import com.azure.core.http.policy.HttpLogOptions;
import com.azure.core.http.policy.RetryOptions;
import com.azure.core.util.BinaryData;
import com.azure.core.util.HttpClientOptions;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 * @author waani
 * @date 2025/2/12
 */
public interface AzureOpenAIClientHelper {


    default OpenAIClientBuilder getOpenAIClientBuilder(String endpoint, OpenAIServiceVersion apiVersion, String apiKey,
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

        return new OpenAIClientBuilder()
                .endpoint(endpoint)
                .serviceVersion(apiVersion)
                .credential(new AzureKeyCredential(apiKey))
                .httpClient(httpClient)
                .httpLogOptions(httpLogOptions)
                .retryOptions(retryOptions);
    }


    default List<ChatRequestMessage> getChatRequestMessage(List<Message> messages) {
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


    default List<ChatCompletionsToolDefinition> toToolDefinitions(List<Tool> tools) {
        return tools.stream()
                .map(this::toToolDefinition)
                .collect(toList());
    }

    default ChatCompletionsToolDefinition toToolDefinition(Tool tool) {
        Function function = tool.getFunction();
        ChatCompletionsFunctionToolDefinitionFunction functionDefinition = new ChatCompletionsFunctionToolDefinitionFunction(function.getName());
        functionDefinition.setDescription(function.getDescription());
        functionDefinition.setParameters(toOpenAiParameters(function.getParameters()));
        return new ChatCompletionsFunctionToolDefinition(functionDefinition);
    }


    default BinaryData toOpenAiParameters(Parameters toolParameters) {
        return BinaryData.fromObject(toolParameters);
    }


    default List<ChatCompletionsToolCall> getChatCompletionsToolCalls(List<ToolCall> toolCalls) {
        if (toolCalls == null || toolCalls.isEmpty()) {
            return Collections.emptyList();
        }
        List<ChatCompletionsToolCall> chatCompletionsToolCalls = new ArrayList<>(toolCalls.size());
        toolCalls.forEach(tc -> {
            ChatCompletionsFunctionToolCall chatCompletionsFunctionToolCall =
                    new ChatCompletionsFunctionToolCall(tc.getId(), new FunctionCall(tc.getFunction().getName(), tc.getFunction().getArguments()));
            chatCompletionsToolCalls.add(chatCompletionsFunctionToolCall);
        });
        return chatCompletionsToolCalls;
    }

}
