package com.aiany.core.message.tool;

import lombok.Data;

import java.io.Serializable;

/**
 * @author waani
 */
@Data
public class ToolCall implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;

    private String name;

    private String arguments;

}
