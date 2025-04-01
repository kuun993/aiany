package com.aiany.openai;

import com.aiany.core.Constants;

public class Test {

    public static void main(String[] args) {
        OpenAiChatModel openAiChatModel = OpenAiChatModel.builder()
        .apiKey(Constants.OPENAI_API_KEY)
        .baseUrl(Constants.OPENAI_BASE_URL)
        .modelName("gpt-4o-mini")
        .build();

        String res = openAiChatModel.chat("你会做什么");
        System.out.println(res);
    }

}
