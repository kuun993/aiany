package com.aiany.azure;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.aiany.core.message.AssistantMessage;
import com.aiany.core.message.Message;
import com.aiany.core.message.UserMessage;
import com.aiany.core.message.tool.ToolCall;
import com.aiany.core.model.ChatModel;
import com.aiany.core.response.Response;
import com.aiany.core.response.Usage;
import com.azure.ai.openai.OpenAIClient;
import com.azure.ai.openai.OpenAIServiceVersion;
import com.azure.ai.openai.models.ChatChoice;
import com.azure.ai.openai.models.ChatCompletions;
import com.azure.ai.openai.models.ChatCompletionsFunctionToolCall;
import com.azure.ai.openai.models.ChatCompletionsOptions;
import com.azure.ai.openai.models.ChatCompletionsToolCall;
import com.azure.ai.openai.models.ChatRequestMessage;
import com.azure.ai.openai.models.ChatResponseMessage;
import com.azure.ai.openai.models.CompletionsUsage;
import com.azure.core.http.ProxyOptions;
import lombok.Builder;

public class AzureOpenAiChatModel implements ChatModel, AzureOpenAIClientHelper {

    private final OpenAIClient openAIClient;
    private final String deploymentName;
    private final Integer maxTokens;
    private final Double temperature;

    @Builder
    public AzureOpenAiChatModel(String deploymentName,
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


    private ChatCompletionsOptions getChatCompletionsOptions(List<ChatRequestMessage> messages) {
        ChatCompletionsOptions options = new ChatCompletionsOptions(messages)
                .setModel(deploymentName)
                .setMaxTokens(maxTokens)
                .setTemperature(temperature);
        return options;
    }


    @Override
    public String chat(String prompt) {
        Response<AssistantMessage> response = chat(UserMessage.create(prompt));
        return response.getContent().getContent();
    }

    @Override
    public Response<AssistantMessage> chat(UserMessage userMessage) {
        return chat(Collections.singletonList(userMessage));
    }

    @Override
    public Response<AssistantMessage> chat(List<Message> messages) {
        ChatCompletionsOptions chatCompletionsOptions = getChatCompletionsOptions(getChatRequestMessage(messages));
        ChatCompletions chatCompletions = openAIClient.getChatCompletions(deploymentName, chatCompletionsOptions);
        ChatChoice chatChoice = chatCompletions.getChoices().get(0);
        return Response.create(handleAssistantMessage(chatChoice.getMessage()),
                handleTokenUsag(chatCompletions.getUsage()),
                chatChoice.getFinishReason().getValue());
    }


    /**
     * 处理 Azure Assistant Message
     *
     * @param chatResponseMessage chatResponseMessage
     * @return AssistantMessage
     */
    private AssistantMessage handleAssistantMessage(ChatResponseMessage chatResponseMessage) {
        List<ChatCompletionsToolCall> chatCompletionsToolCalls = chatResponseMessage.getToolCalls();
        if (chatCompletionsToolCalls == null || chatCompletionsToolCalls.isEmpty()) {
            return AssistantMessage.create(chatResponseMessage.getContent());
        } else {
            List<ToolCall> toolCalls = chatCompletionsToolCalls.stream()
                    .filter(ChatCompletionsFunctionToolCall.class::isInstance)
                    .map(ChatCompletionsFunctionToolCall.class::cast)
                    .map(toolCall -> {
                        return ToolCall.builder()
                                .id(toolCall.getId())
                                .function(com.aiany.core.message.tool.FunctionCall.builder()
                                        .name(toolCall.getFunction().getName())
                                        .arguments(toolCall.getFunction().getArguments())
                                        .build())
                                .build();
                    }).collect(Collectors.toList());
            return AssistantMessage.create(toolCalls);
        }
    }


    /**
     * 处理 Token Usage
     *
     * @param completionsUsage
     * @return
     */
    private Usage handleTokenUsag(CompletionsUsage completionsUsage) {
        if (completionsUsage == null) {
            return null;
        }
        return Usage.builder()
                .promptTokens(completionsUsage.getPromptTokens())
                .completionTokens(completionsUsage.getCompletionTokens())
                .totalTokens(completionsUsage.getTotalTokens())
                .build();
    }

}
