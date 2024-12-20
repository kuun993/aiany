package com.aiany.core.message;

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
        userMessage.role = Role.USER;
        return userMessage;
    }

}
