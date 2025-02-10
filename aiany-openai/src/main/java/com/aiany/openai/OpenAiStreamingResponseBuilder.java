package com.aiany.openai;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.aiany.core.enums.Role;
import com.aiany.core.message.AssistantMessage;
import com.aiany.core.message.tool.FunctionCall;
import com.aiany.core.message.tool.ToolCall;
import com.aiany.core.model.StreamingResponseHandler;
import com.aiany.core.response.ChatCompletionResponse;
import com.aiany.core.response.CompletionChoice;
import com.aiany.core.response.Delta;
import com.aiany.core.response.Response;

public class OpenAiStreamingResponseBuilder {

    private final StringBuffer contentBuilder;

    private final Map<Integer, ToolCallBuilder> toolCallBuilders;

    private String finishReason;

    public OpenAiStreamingResponseBuilder() {
        this.contentBuilder = new StringBuffer();
        this.toolCallBuilders = new HashMap<>();
    }

    /**
     * Build the response
     * 
     * @param chatCompletionResponse chat completion response
     * @param handler handler
     */
    public void append(ChatCompletionResponse chatCompletionResponse, StreamingResponseHandler<AssistantMessage> handler) {

        List<CompletionChoice> choices = chatCompletionResponse.getChoices();
        if (choices == null || choices.isEmpty()) {
            return;
        }

        CompletionChoice choice = choices.get(0);
        // set finishReason
        if (this.finishReason == null && choice.getFinishReason() != null) {
            this.finishReason = choice.getFinishReason();
        }

        Delta delta = choice.getDelta();
        if (delta == null) {
            return;
        }
        String content = delta.getContent();
        if (content != null) {
            this.contentBuilder.append(content);
            handler.onEvent(content);
            return;
        }

        List<ToolCall> toolCalls = delta.getToolCalls();
        if (toolCalls == null || toolCalls.isEmpty()) {
            return;
        }
        ToolCall toolCall = toolCalls.get(0);
        appendToolCall(toolCall);
    }

    /**
     * Append tool call to the builder
     * 
     * @param toolCall tool call
     */
    private void appendToolCall(ToolCall toolCall) {
        ToolCallBuilder toolCallBuilder = toolCallBuilders.computeIfAbsent(toolCall.getIndex(),
                o -> new ToolCallBuilder());
        String id = toolCall.getId();
        if (id != null) {
            toolCallBuilder.idBuilder.append(id);
        }
        FunctionCall function = toolCall.getFunction();
        if (function != null) {
            String name = function.getName();
            if (name != null) {
                toolCallBuilder.nameBuilder.append(name);
            }
            String arguments = function.getArguments();
            if (arguments != null) {
                toolCallBuilder.argumentsBuilder.append(arguments);
            }
        }
    }

    /**
     * 构建流式回复完整输出对象
     * 
     * TODO：tokens的计算
     * 
     * @return  流式回复完整输出对象
     */
    public Response<AssistantMessage> build() {
        Response<AssistantMessage> response = new Response<>();
        response.setFinishReason(finishReason);
        AssistantMessage assistantMessage = new AssistantMessage();
        response.setContent(assistantMessage);
        if (toolCallBuilders.isEmpty()) {
            assistantMessage.setContent(contentBuilder.toString());
            assistantMessage.setRole(Role.ASSISTANT.getRole());
            return response;
        }
        // function calling
        List<ToolCall> toolCalls = new ArrayList<>(toolCallBuilders.size());
        toolCallBuilders.forEach((index, toolCallBuilder) -> {
            FunctionCall functionCall = FunctionCall.builder()
                    .name(toolCallBuilder.nameBuilder.toString())
                    .arguments(toolCallBuilder.argumentsBuilder.toString())
                    .build();
            ToolCall toolCall = ToolCall.builder()
                    .id(toolCallBuilder.idBuilder.toString())
                    .function(functionCall)
                    .build();
            toolCalls.add(toolCall);
        });
        assistantMessage.setToolCalls(toolCalls);
        return response;
    }

    private static class ToolCallBuilder {

        private final StringBuffer idBuilder;

        private final StringBuffer nameBuilder;

        private final StringBuffer argumentsBuilder;

        ToolCallBuilder() {
            this.idBuilder = new StringBuffer();
            this.nameBuilder = new StringBuffer();
            this.argumentsBuilder = new StringBuffer();
        }

    }

}
