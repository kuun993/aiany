package com.aiany.core.exception;

/**
 * @author waani
 */
public class AiAnyException extends RuntimeException {

    public AiAnyException(String message) {
        super(message);
    }

    public AiAnyException(String message, Throwable cause) {
        super(message, cause);
    }

    public AiAnyException(Throwable cause) {
        super(cause);
    }
}
