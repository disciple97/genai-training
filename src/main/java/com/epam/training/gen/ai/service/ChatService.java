package com.epam.training.gen.ai.service;

import com.azure.ai.openai.OpenAIAsyncClient;
import com.azure.ai.openai.OpenAIClientBuilder;
import com.azure.core.credential.AzureKeyCredential;
import com.epam.training.gen.ai.config.OpenAiClientConfig;
import com.microsoft.semantickernel.Kernel;
import com.microsoft.semantickernel.aiservices.openai.chatcompletion.OpenAIChatCompletion;
import com.microsoft.semantickernel.orchestration.InvocationContext;
import com.microsoft.semantickernel.orchestration.InvocationReturnMode;
import com.microsoft.semantickernel.orchestration.PromptExecutionSettings;
import com.microsoft.semantickernel.orchestration.ToolCallBehavior;
import com.microsoft.semantickernel.services.chatcompletion.AuthorRole;
import com.microsoft.semantickernel.services.chatcompletion.ChatCompletionService;
import com.microsoft.semantickernel.services.chatcompletion.ChatHistory;
import com.microsoft.semantickernel.services.chatcompletion.ChatMessageContent;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ChatService {

    private static final Logger logger = LoggerFactory.getLogger(ChatService.class);

    private final ChatCompletionService chatCompletionService;
    private final Kernel kernel;
    private final InvocationContext invocationContext;
    private final ChatHistory chatHistory;

    public ChatService(OpenAiClientConfig openAiClientConfig) {

        OpenAIAsyncClient client = new OpenAIClientBuilder()
                .credential(new AzureKeyCredential(openAiClientConfig.aiKey()))
                .endpoint(openAiClientConfig.aiEndpoint())
                .buildAsyncClient();

        this.chatCompletionService = OpenAIChatCompletion.builder()
                .withModelId(openAiClientConfig.aiDeploymentName())
                .withOpenAIAsyncClient(client)
                .build();

        this.kernel = Kernel.builder()
                .withAIService(ChatCompletionService.class, chatCompletionService)
                .build();

        /* Enable planning */
        this.invocationContext = new InvocationContext.Builder()
                .withReturnMode(InvocationReturnMode.LAST_MESSAGE_ONLY)
                .withToolCallBehavior(ToolCallBehavior.allowAllKernelFunctions(true))
                .withPromptExecutionSettings(
                        PromptExecutionSettings.builder().withTemperature(openAiClientConfig.executionTemperature()).build()
                )
                .build();

        this.chatHistory = new ChatHistory();
        this.chatHistory.addSystemMessage(openAiClientConfig.systemPrompt());
    }

    public String getResponse(String message) {

        logger.trace("User input: {}", message);

        chatHistory.addUserMessage(message);

        /* Prompt AI for response to users input */
        List<ChatMessageContent<?>> results = chatCompletionService
                .getChatMessageContentsAsync(chatHistory, kernel, invocationContext)
                .block();

        if (results == null) {
            logger.trace("Could NOT get AI response on user input");
            return StringUtils.EMPTY;
        }

        var response = results.stream()
                .filter(result -> result.getAuthorRole() == AuthorRole.ASSISTANT && result.getContent() != null)
                .map(ChatMessageContent::getContent)
                .collect(Collectors.joining(" "));

        logger.trace("AI response on user input: {}", response);

        chatHistory.addAssistantMessage(response);

        return response;
    }
}
