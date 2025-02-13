package com.aiany.azure;

import java.time.Duration;
import java.util.List;
import com.aiany.core.message.AssistantMessage;
import com.aiany.core.message.Message;
import com.aiany.core.model.StreamingChatModel;
import com.aiany.core.model.StreamingResponseHandler;
import com.aiany.core.model.token.OpenAiTokenizer;
import com.aiany.core.model.token.Tokenizer;
import com.aiany.core.request.Tool;
import com.aiany.core.response.Response;
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
    private final Tokenizer tokenizer;

    @Builder
    public AzureOpenAiStreamingChatModel(String deploymentName,
            String endpoint,
            OpenAIServiceVersion apiVersion,
            String apiKey,
            ProxyOptions proxyOptions,
            Duration timeout,
            Integer maxRetries,
            Integer maxTokens,
            Double temperature,
            Tokenizer tokenizer) {

        this.deploymentName = deploymentName;
        this.maxTokens = maxTokens;
        this.temperature = temperature;

        this.openAIClient = getOpenAIClientBuilder(endpoint, apiVersion, apiKey, proxyOptions, timeout, maxRetries)
                .buildClient();
        if (tokenizer != null) {
            this.tokenizer = tokenizer;
        } else {
            this.tokenizer = new OpenAiTokenizer();
        }
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
        int inputToken = tokenizer.estimateTokenCountInMessage(messages);
        if (tools != null) {
            inputToken += tokenizer.estimateTokenCountInTool(tools);
        }
        AzureOpenAiStreamingResponseBuilder azureOpenAiStreamingResponseBuilder = new AzureOpenAiStreamingResponseBuilder(
                inputToken);
        openAIClient.getChatCompletionsStream(deploymentName, options)
                .stream()
                .forEach(chatCompletions -> {
                    azureOpenAiStreamingResponseBuilder.append(chatCompletions, handler);
                });
        Response<AssistantMessage> response = azureOpenAiStreamingResponseBuilder.build(tokenizer);
        handler.onComplete(response);
    }

}
