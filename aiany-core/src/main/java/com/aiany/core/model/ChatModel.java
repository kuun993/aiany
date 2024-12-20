package com.aiany.core.model;

import com.aiany.core.message.AssistantMessage;
import com.aiany.core.message.Message;
import com.aiany.core.message.UserMessage;
import com.aiany.core.response.ChatCompletionResponse;
import com.aiany.core.response.Response;

import java.util.List;

/**
 * @author waani
 */
public interface ChatModel {


    /**
     * 对话
     *
     * @param prompt 提示词
     * @return 对话结果
     */
    String chat(String prompt);

    Response<AssistantMessage> chat(UserMessage userMessage);

    Response<AssistantMessage> chat(List<Message> messages);

    default Response<AssistantMessage> toResponse(ChatCompletionResponse chatCompletionResponse) {
        Response<AssistantMessage> response = new Response<>();
        response.setUsage(chatCompletionResponse.getUsage());
        // TODO function calling
        response.setContent(AssistantMessage.create(chatCompletionResponse.getResult()));
        return response;
    }

}
