package com.aiany.core.message;

import lombok.Data;
import lombok.EqualsAndHashCode;
import java.util.List;
import com.aiany.core.message.tool.ToolCall;

/**
 * @author waani
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class AssistantMessage extends Message {

    private List<ToolCall> toolCalls;

    /**
     * 普通回复
     * @param message   消息
     * @return  AssistantMessage
     */
    public static AssistantMessage create(String message) {
        AssistantMessage assistantMessage = new AssistantMessage();
        assistantMessage.content = message;
        assistantMessage.role = Role.ASSISTANT;
        return assistantMessage;
    }


    /**
     * 创建工具调用
     * @param toolCalls   工具函数
     * @return  AssistantMessage    AssistantMessage
     */
    public static AssistantMessage create(List<ToolCall> toolCalls) {
        AssistantMessage assistantMessage = new AssistantMessage();
        assistantMessage.toolCalls = toolCalls;
        assistantMessage.role = Role.ASSISTANT;
        return assistantMessage;
    }

}
