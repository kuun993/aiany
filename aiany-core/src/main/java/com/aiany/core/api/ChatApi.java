package com.aiany.core.api;

import com.aiany.core.request.ChatCompletionRequest;
import com.aiany.core.response.ChatCompletionResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

/**
 * @author waani
 */
public interface ChatApi {

    @POST("chat/completions")
    @Headers({"Content-Type: application/json"})
    Call<ChatCompletionResponse> chatCompletions(@Body ChatCompletionRequest chatCompletionRequest);

}
