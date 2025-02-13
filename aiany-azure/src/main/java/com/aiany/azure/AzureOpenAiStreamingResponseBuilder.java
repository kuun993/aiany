package com.aiany.azure;



import com.aiany.core.message.AssistantMessage;
import com.aiany.core.model.StreamingResponseBuilder;
import com.aiany.core.model.StreamingResponseHandler;
import com.azure.ai.openai.models.*;

import java.util.List;

public class AzureOpenAiStreamingResponseBuilder extends StreamingResponseBuilder {


    /**
     * Append chat completions to the response builder
     *
     * @param chatCompletions   chat completions
     * @param handler        handler
     */
    public void append(ChatCompletions chatCompletions, StreamingResponseHandler<AssistantMessage> handler) {

        final List<ChatChoice> choices = chatCompletions.getChoices();
        if (choices == null || choices.isEmpty()) {
            return;
        }
        final ChatChoice choice = choices.get(0);
        // set finishReason
        if (this.finishReason == null && choice.getFinishReason() != null) {
            this.finishReason = choice.getFinishReason().getValue();
        }

        final ChatResponseMessage delta = choice.getDelta();
        if (delta == null) {
            return;
        }
        String content = delta.getContent();
        if (content != null) {
            this.contentBuilder.append(content);
            if (handler != null) {
                handler.onEvent(content);
            }
            return;
        }

        final List<ChatCompletionsToolCall> toolCalls = delta.getToolCalls();
        if (toolCalls == null || toolCalls.isEmpty()) {
            return;
        }

        toolCalls.forEach(this::appendToolCall);
    }


    /**
     * Append tool call to the builder
     *
     * @param toolCall  tool call
     */
    private void appendToolCall(ChatCompletionsToolCall toolCall) {
        final String index = toolCall.getId();
        ToolCallBuilder toolCallBuilder = toolCallBuilders.computeIfAbsent(index,
                o -> new ToolCallBuilder());

        String id = toolCall.getId();
        if (id != null) {
            toolCallBuilder.getIdBuilder().append(id);
        }
        if (toolCall instanceof ChatCompletionsFunctionToolCall) {
            ChatCompletionsFunctionToolCall functionCall = (ChatCompletionsFunctionToolCall) toolCall;
            if (functionCall.getFunction().getName() != null) {
                toolCallBuilder.getNameBuilder().append(functionCall.getFunction().getName());
            }
            if (functionCall.getFunction().getArguments() != null) {
                toolCallBuilder.getArgumentsBuilder().append(functionCall.getFunction().getArguments());
            }
        }
    }


}
