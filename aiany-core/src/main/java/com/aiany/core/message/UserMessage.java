package com.aiany.core.message;

import com.aiany.core.enums.Role;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author waani
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class UserMessage extends Message {

    public static UserMessage create(String message) {
        UserMessage userMessage = new UserMessage();
        userMessage.content = message;
        userMessage.role = Role.USER.getRole();
        return userMessage;
    }

}
