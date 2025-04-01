package com.aiany.core.utils;

public class DefaultUtil {

    private DefaultUtil() {
    }


    public static <T> T getOrDefault(T value, T defaultValue) {
        return value != null ? value : defaultValue;
    }

}
