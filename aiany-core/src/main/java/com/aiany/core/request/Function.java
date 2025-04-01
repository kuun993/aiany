package com.aiany.core.request;

import lombok.Data;

import java.io.Serializable;

/**
 * @author waani
 */
@Data
public class Function implements Serializable {

    private static final long serialVersionUID = 1L;

    private String name;

    private String description;

    private Parameters parameters;
}
