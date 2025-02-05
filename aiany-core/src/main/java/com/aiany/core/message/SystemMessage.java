package com.aiany.core.message;

import com.aiany.core.enums.Role;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author waani
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class SystemMessage extends Message {

    public static SystemMessage create(String message) {
        SystemMessage systemMessage = new SystemMessage();
        systemMessage.content = message;
        systemMessage.role = Role.SYSTEM.getRole();
        return systemMessage;
    }

}
