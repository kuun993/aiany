package com.aiany.core.request;

import com.aiany.core.message.Message;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @author waani
 */
@Data
public class ChatCompletionRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    private String model;

    private List<Message> messages;

    private Double temperature;

    private Double topP;

    private Integer n;

    private Boolean stream;

    private List<String> stop;

    private Integer maxTokens;

    private Double presencePenalty;

    private Double frequencyPenalty;

    private Map<String, Integer> logitBias;

    private String user;

    private ResponseFormat responseFormat;

    private Integer seed;

    private List<Tool> tools;

    private Tool toolChoice;


    public ChatCompletionRequest() {}


    @Builder
    public ChatCompletionRequest(String model, List<Message> messages, Double temperature, Double topP, Integer n, Boolean stream, List<String> stop, Integer maxTokens, Double presencePenalty, Double frequencyPenalty, Map<String, Integer> logitBias, String user, ResponseFormat responseFormat, Integer seed, List<Tool> tools, Tool toolChoice) {
        this.model = model;
        this.messages = messages;
        this.temperature = temperature;
        this.topP = topP;
        this.n = n;
        this.stream = stream;
        this.stop = stop;
        this.maxTokens = maxTokens;
        this.presencePenalty = presencePenalty;
        this.frequencyPenalty = frequencyPenalty;
        this.logitBias = logitBias;
        this.user = user;
        this.responseFormat = responseFormat;
        this.seed = seed;
        this.tools = tools;
        this.toolChoice = toolChoice;
    }

}
