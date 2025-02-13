package com.aiany.core.model;

import com.aiany.core.enums.Role;
import com.aiany.core.message.AssistantMessage;
import com.aiany.core.message.tool.FunctionCall;
import com.aiany.core.message.tool.ToolCall;
import com.aiany.core.response.Response;
import lombok.Getter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author waani
 * @date 2025/2/13
 */
public abstract class StreamingResponseBuilder {

    protected String finishReason;

    protected final StringBuffer contentBuilder;

    protected final Map<Object, ToolCallBuilder> toolCallBuilders;

    protected StreamingResponseBuilder() {
        this.contentBuilder = new StringBuffer();
        this.toolCallBuilders = new HashMap<>();
    }


    /**
     * 构建流式请求完整回复
     *
     * TODO: token
     *
     * @return Response<AssistantMessage>
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
            com.aiany.core.message.tool.FunctionCall functionCall = FunctionCall.builder()
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



    @Getter
    protected static class ToolCallBuilder {

        protected final StringBuffer idBuilder;

        private final StringBuffer nameBuilder;

        private final StringBuffer argumentsBuilder;

        public ToolCallBuilder() {
            this.idBuilder = new StringBuffer();
            this.nameBuilder = new StringBuffer();
            this.argumentsBuilder = new StringBuffer();
        }

    }

}
