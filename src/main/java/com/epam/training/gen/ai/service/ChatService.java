package com.epam.training.gen.ai.service;

import com.epam.training.gen.ai.config.OpenAiClientConfig;
import com.microsoft.semantickernel.orchestration.InvocationContext;
import com.microsoft.semantickernel.orchestration.InvocationReturnMode;
import com.microsoft.semantickernel.orchestration.PromptExecutionSettings;
import com.microsoft.semantickernel.orchestration.ToolCallBehavior;
import com.microsoft.semantickernel.services.chatcompletion.ChatHistory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;

@Slf4j
@Service
public class ChatService {

    private static final String DEFAULT_DEPLOYMENT_NAME = "default";

    private final OpenAiClientConfig openAiClientConfig;
    private final ChatCompletable defaultChatCompletion;
    private final Map<String, ChatCompletable> chatCompletions;
    private final InvocationContext invocationContext;
    private final EmbeddingService embeddingService;

    private ChatHistory chatHistory;

    public ChatService(OpenAiClientConfig openAiClientConfig, Map<String, ChatCompletable> chatCompletions, EmbeddingService embeddingService) {

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

        this.embeddingService = embeddingService;
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
        resetChatHistory(null);
    }

    public void resetChatHistory(@Nullable String extraSystemPrompt) {
        chatHistory = new ChatHistory();
        var systemPrompt = openAiClientConfig.getSystemPrompt();
        if (StringUtils.hasLength(systemPrompt)) {
            chatHistory.addSystemMessage(systemPrompt);
        }
        if (StringUtils.hasLength(extraSystemPrompt)) {
            chatHistory.addSystemMessage(extraSystemPrompt);
        }
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

    public String getRagResponse(String deploymentName, String message) {
        var context = embeddingService.search(message);
        log.trace("Found context: {}", context);
        resetChatHistory(context.value());
        return getResponse(deploymentName, message);
    }
}
