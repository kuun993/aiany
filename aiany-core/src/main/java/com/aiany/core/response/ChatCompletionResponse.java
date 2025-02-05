package com.aiany.core.response;

import com.aiany.core.message.AssistantMessage;
import com.aiany.core.message.tool.ToolCall;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author waani
 * @date 2024/9/2
 */
@EqualsAndHashCode(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@NoArgsConstructor
public class ChatCompletionResponse extends ErrorInfo {

    /**
     * id
     */
    private String id;

    /**
     * created
     */
    private Integer created;

    /**
     * model
     */
    private String model;

    /**
     * choices
     */
    private List<CompletionChoice> choices;


    /**
     * finishReason
     */
    private String finishReason;

    /**
     * usage
     */
    private Usage usage;

    @Builder
    public ChatCompletionResponse(String id, Integer created, String model, List<CompletionChoice> choices, Usage usage) {
        this.id = id;
        this.created = created;
        this.model = model;
        this.choices = choices;
        this.usage = usage;
    }


    /**
     * 机器人回复结果
     */
    public String getResult() {
        return this.choices.get(0).getMessage().getContent();
    }


    /**
     * 是否函数调用
     * @return  是否函数调用
     */
    public boolean isFunctionCalling() {
        final AssistantMessage message = this.choices.get(0).getMessage();
        final List<ToolCall> toolCalls = message.getToolCalls();
        return toolCalls != null && !toolCalls.isEmpty();
    }


    /**
     * 获取助手消息
     * @return  AssistantMessage
     */
    public AssistantMessage getAssistantMessage() {
        return this.choices.get(0).getMessage();
    }


}
