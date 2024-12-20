package com.aiany.core.request;

import lombok.Data;

import java.io.Serializable;

/**
 * @author waani
 */
@Data
public class Tool implements Serializable {

    private static final long serialVersionUID = 1L;

    private String type = "function";

    private Function function;
}
