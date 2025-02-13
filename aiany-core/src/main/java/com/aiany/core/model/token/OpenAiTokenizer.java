package com.aiany.core.model.token;

import java.util.List;
import java.util.Map;

import com.aiany.core.message.AssistantMessage;
import com.aiany.core.message.Message;
import com.aiany.core.message.tool.FunctionCall;
import com.aiany.core.message.tool.ToolCall;
import com.aiany.core.request.Function;
import com.aiany.core.request.Parameters;
import com.aiany.core.request.Tool;
import com.knuddels.jtokkit.Encodings;
import com.knuddels.jtokkit.api.Encoding;
import com.knuddels.jtokkit.api.EncodingRegistry;
import com.knuddels.jtokkit.api.ModelType;

public class OpenAiTokenizer implements Tokenizer {

    private static final ModelType defaultModelType = ModelType.GPT_4;

    private final Encoding encoding;

    public OpenAiTokenizer() {
        this(defaultModelType);
    }

    public OpenAiTokenizer(String modelName) {
        this(ModelType.fromName(modelName).orElse(defaultModelType));
    }

    public OpenAiTokenizer(ModelType modelType) {
        EncodingRegistry registry = Encodings.newDefaultEncodingRegistry();
        this.encoding = registry.getEncodingForModel(modelType);
    }

    @Override
    public int estimateTokenCountInText(String text) {
        if (text == null || text.isEmpty()) {
            return 0;
        }
        return encoding.countTokens(text);
    }

    @Override
    public int estimateTokenCountInMessage(Message message) {
        if (message instanceof AssistantMessage) {
            int tokenCount = estimateTokenCountInText(message.getContent());
            AssistantMessage assistantMessage = (AssistantMessage) message;
            List<ToolCall> toolCalls = assistantMessage.getToolCalls();
            if (toolCalls == null || toolCalls.isEmpty()) {
                return tokenCount;
            }
            for (ToolCall toolCall : toolCalls) {
                FunctionCall function = toolCall.getFunction();
                tokenCount += (estimateTokenCountInText(function.getName())) * 2;
                tokenCount += estimateTokenCountInText(function.getArguments());
            }
            return tokenCount;
        }
        return estimateTokenCountInText(message.getContent());
    }

    @Override
    public int estimateTokenCountInMessage(List<Message> messages) {
        return messages.stream().mapToInt(this::estimateTokenCountInMessage).sum();
    }

    @Override
    public int estimateTokenCountInTool(Tool tool) {
        Function function = tool.getFunction();
        int tokenCount = estimateTokenCountInText(function.getName());
        tokenCount += estimateTokenCountInText(function.getDescription());
        tokenCount += estimateTokenCountInToolParameters(function.getParameters());
        return tokenCount;
    }

    @Override
    public int estimateTokenCountInTool(List<Tool> tools) {
        return tools.stream().mapToInt(this::estimateTokenCountInTool).sum();
    }


    private int estimateTokenCountInToolParameters(Parameters parameters) {
        if (parameters == null) {
            return 0;
        }

        int tokenCount = 3;
        Map<String, Map<String, Object>> properties = parameters.getProperties();
        for (String property : properties.keySet()) {
            tokenCount += estimateTokenCountInText(property);
            for (Map.Entry<String, Object> entry : properties.get(property).entrySet()) {
                if ("type".equals(entry.getKey())) {
                    tokenCount += 1;
                } else if ("description".equals(entry.getKey())) {
                    tokenCount += estimateTokenCountInText(entry.getValue().toString());
                } else if ("enum".equals(entry.getKey())) {
                    for (Object enumValue : (Object[]) entry.getValue()) {
                        tokenCount += 3;
                        tokenCount += estimateTokenCountInText(enumValue.toString());
                    }
                }
            }
        }
        return tokenCount;
    }
    

}
