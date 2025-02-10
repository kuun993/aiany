package com.aiany.core.model;

import com.aiany.core.response.Response;

public interface StreamingResponseHandler<T> {

    void onEvent(String token);
 
    default void onComplete(Response<T> response) {
    }

    void onFailure(Throwable error);

}
