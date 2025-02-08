package com.aiany.core.model;

import java.util.List;

import com.aiany.core.message.AssistantMessage;
import com.aiany.core.message.Message;
import com.aiany.core.request.Tool;

public interface StreamingChatModel {


    default String endTag() {
        return "[DONE]";
    }


    void chat(List<Message> messages, List<Tool> tools, StreamingResponseHandler<AssistantMessage> handler);

}
