package com.aiany.core.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author waani
 * @date 2024/9/2
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@NoArgsConstructor
public class Usage {

    /**
     * promptTokens
     */
    @JsonProperty("prompt_tokens")
    private Integer promptTokens;

    /**
     * completionTokens
     */
    @JsonProperty("completion_tokens")
    private Integer completionTokens;

    /**
     * totalTokens
     */
    @JsonProperty("total_tokens")
    private Integer totalTokens;


    @JsonProperty("prompt_tokens_details")
    private PromptTokensDetails promptTokensDetails;


    @Builder
    public Usage(Integer promptTokens, Integer completionTokens, Integer totalTokens, PromptTokensDetails promptTokensDetails) {
        this.promptTokens = promptTokens;
        this.completionTokens = completionTokens;
        this.totalTokens = totalTokens;
        this.promptTokensDetails = promptTokensDetails;
    }


    @Data
    public static class PromptTokensDetails {

        @JsonProperty("cached_tokens")
        private Integer cachedTokens;

        @JsonProperty("audio_tokens")
        private Integer audioTokens;

        @Builder
        public PromptTokensDetails(Integer cachedTokens, Integer audioTokens) {
            this.cachedTokens = cachedTokens;
            this.audioTokens = audioTokens;
        }

    }


}
