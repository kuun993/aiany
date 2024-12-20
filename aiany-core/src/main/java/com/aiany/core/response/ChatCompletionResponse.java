package com.aiany.core.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author waani
 * @date 2024/9/2
 */
@EqualsAndHashCode(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@NoArgsConstructor
public class ChatCompletionResponse extends ErrorInfo {

    /**
     * id
     */
    private String id;

    /**
     * created
     */
    private Integer created;

    /**
     * model
     */
    private String model;

    /**
     * choices
     */
    private List<CompletionChoice> choices;

    /**
     * usage
     */
    private Usage usage;

    @Builder
    public ChatCompletionResponse(String id, Integer created, String model, List<CompletionChoice> choices, Usage usage) {
        this.id = id;
        this.created = created;
        this.model = model;
        this.choices = choices;
        this.usage = usage;
    }


    /**
     * 机器人回复结果
     */
    public String getResult() {
        return this.choices.get(0).getMessage().getContent();
    }

}
