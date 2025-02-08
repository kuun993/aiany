package com.aiany.core.message.tool;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * @author waani
 */
@Builder
@Data
public class ToolCall implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;

    private FunctionCall function;
    
    private Integer index;
}
