package com.aiany.core.request;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import lombok.Data;

/**
 * @author waani
 */
@Data
public class Parameters implements Serializable {

    private static final long serialVersionUID = 1L;

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
