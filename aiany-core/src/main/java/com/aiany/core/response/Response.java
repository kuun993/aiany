package com.aiany.core.response;

import lombok.Data;

/**
 * @author waani
 * @date 2024/9/4
 */
@Data
public class Response<T> {

    private T content;

    private Usage usage;

    private String finishReason;

    public static <T> Response<T> create(T content, Usage usage, String finishReason) {
        Response<T> response = new Response<>();
        response.setContent(content);
        response.setUsage(usage);
        response.setFinishReason(finishReason);
        return response;
    }

}
