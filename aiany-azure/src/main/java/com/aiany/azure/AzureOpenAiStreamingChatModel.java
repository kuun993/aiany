package com.aiany.azure;

import java.time.Duration;
import java.util.List;
import com.aiany.core.message.AssistantMessage;
import com.aiany.core.message.Message;
import com.aiany.core.model.StreamingChatModel;
import com.aiany.core.model.StreamingResponseHandler;
import com.aiany.core.request.Tool;
import com.azure.ai.openai.OpenAIClient;
import com.azure.ai.openai.OpenAIServiceVersion;
import com.azure.ai.openai.models.ChatCompletionsOptions;
import com.azure.core.http.ProxyOptions;
import lombok.Builder;

public class AzureOpenAiStreamingChatModel implements StreamingChatModel, AzureOpenAIClientHelper {

    private final OpenAIClient openAIClient;
    private final String deploymentName;
    private final Integer maxTokens;
    private final Double temperature;

    @Builder
    public AzureOpenAiStreamingChatModel(String deploymentName,
                                         String endpoint,
                                         OpenAIServiceVersion apiVersion,
                                         String apiKey,
                                         ProxyOptions proxyOptions,
                                         Duration timeout,
                                         Integer maxRetries,
                                         Integer maxTokens,
                                         Double temperature) {

        this.deploymentName = deploymentName;
        this.maxTokens = maxTokens;
        this.temperature = temperature;

        this.openAIClient = getOpenAIClientBuilder(endpoint, apiVersion, apiKey, proxyOptions, timeout, maxRetries)
                .buildClient();
    }


    @Override
    public void chat(List<Message> messages, List<Tool> tools, StreamingResponseHandler<AssistantMessage> handler) {

        ChatCompletionsOptions options = new ChatCompletionsOptions(getChatRequestMessage(messages))
                .setModel(deploymentName)
                .setMaxTokens(maxTokens)
                .setTemperature(temperature);

        if (tools != null && !tools.isEmpty()) {
            options.setTools(toToolDefinitions(tools));
        }

        AzureOpenAiStreamingResponseBuilder azureOpenAiStreamingResponseBuilder = new AzureOpenAiStreamingResponseBuilder();
        openAIClient.getChatCompletionsStream(deploymentName, options)
                .stream()
                .forEach(chatCompletions -> {
                    azureOpenAiStreamingResponseBuilder.append(chatCompletions, handler);
                });
    }


}
