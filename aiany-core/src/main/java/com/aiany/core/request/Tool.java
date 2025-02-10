package com.aiany.core.request;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author waani
 */
@Data
public class Tool implements Serializable {

    private static final long serialVersionUID = 1L;

    private String type = "function";

    private Function function;



    public static class ToolBuilder {

        private String name;

        private String description;

        private Map<String, Map<String, Object>> properties;

        private List<String> required;

        private ToolBuilder() {}

        public static ToolBuilder builder() {
            return new ToolBuilder();
        }

        public ToolBuilder name(String name) {
            this.name = name;
            return this;
        }

        public ToolBuilder description(String description) {
            this.description = description;
            return this;
        }

        public ToolBuilder addProperty(String field, String type, boolean isRequired) {
            if (properties == null) {
                properties = new HashMap<>();
                required = new ArrayList<>();
            }
            Map<String, Object> map = new HashMap<>();
            map.put("type", type);
            properties.put(field, map);
            if (isRequired) {
                required.add(field);
            }
            return this;
        }


        public Tool build() {
            Tool tool = new Tool();
            Function function = new Function();
            tool.setFunction(function);
            function.setName(this.name);
            function.setDescription(this.description);
            Parameters parameters = new Parameters();
            parameters.setProperties(properties);
            parameters.setRequired(required);
            function.setParameters(parameters);
            return tool;
        }


    }

}
