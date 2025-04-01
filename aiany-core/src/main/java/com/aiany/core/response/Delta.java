package com.aiany.core.response;

import java.util.List;

import com.aiany.core.message.tool.ToolCall;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class Delta {

    private String role;

    private String content;

    private List<ToolCall> toolCalls;

}
