package com.aiany.core.message;

import lombok.Data;

import java.io.Serializable;

/**
 * @author waani
 */
@Data
public abstract class Message implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * role
     */
    protected String role;

    /**
     * content
     */
    protected String content;

    /**
     * refusal
     */
    protected String refusal;


}
