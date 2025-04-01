package com.aiany.core.message.tool;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class FunctionCall {

    private String name;
    
    private String arguments;
}
