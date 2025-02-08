package com.aiany.core.model;

import com.aiany.core.response.Response;

public interface StreamingResponseHandler<T> {

    void onNext(String token);
 
    default void onComplete(Response<T> response) {
    }

    void onError(Throwable error);

}
