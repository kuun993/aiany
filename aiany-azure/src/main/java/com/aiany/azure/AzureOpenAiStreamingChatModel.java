package com.aiany.azure;

import java.util.List;

import com.aiany.core.message.AssistantMessage;
import com.aiany.core.message.Message;
import com.aiany.core.model.StreamingChatModel;
import com.aiany.core.model.StreamingResponseHandler;
import com.aiany.core.request.Tool;

public class AzureOpenAiStreamingChatModel implements StreamingChatModel {

    @Override
    public void chat(List<Message> messages, List<Tool> tools, StreamingResponseHandler<AssistantMessage> handler) {
        
    }


}
