package com.aiany.core.model;

import java.util.List;

import com.aiany.core.message.AssistantMessage;
import com.aiany.core.message.Message;
import com.aiany.core.message.UserMessage;
import com.aiany.core.request.Tool;

public interface StreamingChatModel {

    default String endTag() {
        return "[DONE]";
    }

    default void chat(String message, StreamingResponseHandler<AssistantMessage> handler) {
        UserMessage userMessage = UserMessage.create(message);
        chat(List.of(userMessage), null, handler);
    }

    default void chat(List<Message> messages, StreamingResponseHandler<AssistantMessage> handler) {
        chat(messages, null, handler);
    }

    void chat(List<Message> messages, List<Tool> tools, StreamingResponseHandler<AssistantMessage> handler);

}
