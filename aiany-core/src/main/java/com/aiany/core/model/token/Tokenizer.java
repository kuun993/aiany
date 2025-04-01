package com.aiany.core.model.token;

import java.util.List;

import com.aiany.core.message.Message;
import com.aiany.core.request.Tool;

public interface Tokenizer {

    int estimateTokenCountInText(String text);

    int estimateTokenCountInMessage(Message message);

    int estimateTokenCountInMessage(List<Message> messages);

    int estimateTokenCountInTool(Tool tool);

    int estimateTokenCountInTool(List<Tool> tools);

}
