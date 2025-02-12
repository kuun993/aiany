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

    /**
     * 对话
     *
     * @param userMessage 用户消息
     * @return 对话结果
     */
    Response<AssistantMessage> chat(UserMessage userMessage);

    /**
     * 对话
     * 
     * @param messages
     * @return
     */
    Response<AssistantMessage> chat(List<Message> messages);

    /**
     * toResponse
     * 
     * @param chatCompletionResponse
     * @return
     */
    default Response<AssistantMessage> toResponse(ChatCompletionResponse chatCompletionResponse) {
        return Response.create(chatCompletionResponse.getAssistantMessage(), chatCompletionResponse.getUsage(), chatCompletionResponse.getFinishReason());
    }

}
