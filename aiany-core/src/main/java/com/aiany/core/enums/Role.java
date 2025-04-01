package com.aiany.core.enums;

import lombok.Getter;

@Getter
public enum Role {

    SYSTEM("system"),

    ASSISTANT("assistant"),

    USER("user");
    

    private final String role;


    Role(String role) {
        this.role = role;
    }

}
