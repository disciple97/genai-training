package com.epam.training.gen.ai.service;

import com.epam.training.gen.ai.config.OpenAiClientConfig;
import com.microsoft.semantickernel.orchestration.InvocationContext;
import com.microsoft.semantickernel.orchestration.InvocationReturnMode;
import com.microsoft.semantickernel.orchestration.PromptExecutionSettings;
import com.microsoft.semantickernel.orchestration.ToolCallBehavior;
import com.microsoft.semantickernel.services.chatcompletion.ChatHistory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;

@Slf4j
@Service
public class ChatService {

    private static final String DEFAULT_DEPLOYMENT_NAME = "default";

    private final OpenAiClientConfig openAiClientConfig;
    private final ChatCompletable defaultChatCompletion;
    private final Map<String, ChatCompletable> chatCompletions;
    private final InvocationContext invocationContext;

    private ChatHistory chatHistory;

    public ChatService(OpenAiClientConfig openAiClientConfig, Map<String, ChatCompletable> chatCompletions) {

        this.openAiClientConfig = openAiClientConfig;

        var deploymentName = openAiClientConfig.getDeploymentName();

        this.defaultChatCompletion = chatCompletions.get(deploymentName);
        if (this.defaultChatCompletion == null) {
            throw new RuntimeException(String.format("Invalid default deployment name: %s", deploymentName));
        }

        this.chatCompletions = chatCompletions;

        /* Enable planning */
        this.invocationContext = new InvocationContext.Builder()
                .withReturnMode(InvocationReturnMode.LAST_MESSAGE_ONLY)
                .withToolCallBehavior(ToolCallBehavior.allowAllKernelFunctions(true))
                .withPromptExecutionSettings(
                        PromptExecutionSettings.builder().withTemperature(openAiClientConfig.getExecutionTemperature()).build()
                )
                .build();

        resetChatHistory();
    }

    /**
     * Retrieve names of all supported deployments.
     *
     * @return Set of supported deployment names
     */
    public Set<String> getDeploymentNames() {
        return chatCompletions.keySet();
    }

    /**
     * Reset chat history and set default system prompt.
     */
    public void resetChatHistory() {
        chatHistory = new ChatHistory();
        chatHistory.addSystemMessage(openAiClientConfig.getSystemPrompt());
    }

    /**
     * Retrieve AI response on user input.
     *
     * @param deploymentName Deployment name that should provide AI response
     * @param message        User input message
     * @return AI response on provided user input message
     */
    public String getResponse(String deploymentName, String message) {

        var chatCompletion = getChatCompletion(deploymentName);

        log.trace("User input: {}", message);

        chatHistory.addUserMessage(message);

        var response = chatCompletion.getResponse(invocationContext, chatHistory);

        log.trace("AI response: {}", response);

        chatHistory.addAssistantMessage(response);

        return response;
    }

    private ChatCompletable getChatCompletion(String deploymentName) {
        if (DEFAULT_DEPLOYMENT_NAME.equals(deploymentName)) {
            return defaultChatCompletion;
        }
        return chatCompletions.getOrDefault(deploymentName, defaultChatCompletion);
    }
}
