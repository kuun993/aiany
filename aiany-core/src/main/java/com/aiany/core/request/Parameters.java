package com.aiany.core.request;

import java.util.List;
import java.util.Map;

import lombok.Data;

/**
 * @author waani
 */
@Data
public class Parameters {

    private String type = "object";

    /**
     * 参数
     */
    private Map<String, Map<String, Object>> properties;

    /**
     * 参数必填
     */
    private List<String> required;
}
