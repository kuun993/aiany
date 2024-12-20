package com.aiany.core.response;

import com.aiany.core.message.AssistantMessage;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

/**
 * 
 * @author waani
 * @date 2024/9/2
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class CompletionChoice {

    /**
     * 选项列表中选项的索引
     */
    private Integer index;

    /**
     * 模型停止生成令牌的原因
     */
    private String finishReason;

    /**
     * 模型生成的补全消息
     */
    private AssistantMessage message;


}
