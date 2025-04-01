package com.aiany.core.message;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author waani
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ToolResultMessage extends Message {

    private static final long serialVersionUID = 1L;

    private String id;

    private String toolName;

}
