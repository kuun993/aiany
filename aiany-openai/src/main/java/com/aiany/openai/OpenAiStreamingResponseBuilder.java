package com.aiany.openai;

import java.util.List;
import com.aiany.core.message.AssistantMessage;
import com.aiany.core.message.tool.FunctionCall;
import com.aiany.core.message.tool.ToolCall;
import com.aiany.core.model.StreamingResponseBuilder;
import com.aiany.core.model.StreamingResponseHandler;
import com.aiany.core.response.ChatCompletionResponse;
import com.aiany.core.response.CompletionChoice;
import com.aiany.core.response.Delta;

public class OpenAiStreamingResponseBuilder extends StreamingResponseBuilder {

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
            if (handler != null) {
                handler.onEvent(content);
            }
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
            toolCallBuilder.getIdBuilder().append(id);
        }
        FunctionCall function = toolCall.getFunction();
        if (function != null) {
            String name = function.getName();
            if (name != null) {
                toolCallBuilder.getNameBuilder().append(name);
            }
            String arguments = function.getArguments();
            if (arguments != null) {
                toolCallBuilder.getArgumentsBuilder().append(arguments);
            }
        }
    }


}
