package com.aiany.core.message.tool;

import lombok.Data;

@Data
public class FunctionCall {

    private String name;
    
    private String arguments;
}
