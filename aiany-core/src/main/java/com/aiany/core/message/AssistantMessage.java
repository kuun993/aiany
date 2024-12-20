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

    public static AssistantMessage create(String message) {
        AssistantMessage assistantMessage = new AssistantMessage();
        assistantMessage.content = message;
        assistantMessage.role = Role.ASSISTANT;
        return assistantMessage;
    }

}
