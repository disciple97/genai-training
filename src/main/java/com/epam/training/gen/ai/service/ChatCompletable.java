package com.epam.training.gen.ai.service;

import com.microsoft.semantickernel.orchestration.InvocationContext;
import com.microsoft.semantickernel.services.chatcompletion.ChatHistory;

public interface ChatCompletable {

    /**
     * Retrieve chat completion name.
     */
    String getName();

    /**
     * Retrieve chat response.
     */
    String getResponse(InvocationContext invocationContext, ChatHistory chatHistory);
}
