package com.epam.training.gen.ai.service;

import com.google.gson.Gson;

import com.azure.ai.openai.OpenAIAsyncClient;
import com.epam.training.gen.ai.config.DeploymentConfig;
import com.epam.training.gen.ai.service.plugin.DateTimePlugin;
import com.epam.training.gen.ai.service.plugin.LightModel;
import com.epam.training.gen.ai.service.plugin.LightsPlugin;
import com.microsoft.semantickernel.Kernel;
import com.microsoft.semantickernel.aiservices.openai.chatcompletion.OpenAIChatCompletion;
import com.microsoft.semantickernel.contextvariables.ContextVariableTypeConverter;
import com.microsoft.semantickernel.contextvariables.ContextVariableTypes;
import com.microsoft.semantickernel.orchestration.InvocationContext;
import com.microsoft.semantickernel.plugin.KernelPluginFactory;
import com.microsoft.semantickernel.services.chatcompletion.AuthorRole;
import com.microsoft.semantickernel.services.chatcompletion.ChatCompletionService;
import com.microsoft.semantickernel.services.chatcompletion.ChatHistory;
import com.microsoft.semantickernel.services.chatcompletion.ChatMessageContent;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.stream.Collectors;

@Slf4j
public class ChatCompletion implements ChatCompletable {

    private final String name;
    private final ChatCompletionService chatCompletionService;
    private final Kernel kernel;

    public ChatCompletion(
            DeploymentConfig deploymentConfig, OpenAIAsyncClient openAIAsyncClient, DateTimePlugin dateTimePlugin, LightsPlugin lightsPlugin
    ) {

        this.name = deploymentConfig.name();

        this.chatCompletionService = OpenAIChatCompletion.builder()
                .withModelId(deploymentConfig.model())
                .withOpenAIAsyncClient(openAIAsyncClient)
                .build();

        this.kernel = Kernel.builder()
                .withAIService(ChatCompletionService.class, chatCompletionService)
                .withPlugin(KernelPluginFactory.createFromObject(dateTimePlugin, "DateTime"))
                .withPlugin(KernelPluginFactory.createFromObject(lightsPlugin, "Lights"))
                .build();

        ContextVariableTypes.addGlobalConverter(ContextVariableTypeConverter.builder(LightModel.class).toPromptString(new Gson()::toJson).build());
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getResponse(InvocationContext invocationContext, ChatHistory chatHistory) {

        /* Prompt AI for response to users input */
        var results = chatCompletionService.getChatMessageContentsAsync(chatHistory, kernel, invocationContext).block();

        if (results == null) {
            log.error("[{}] Could NOT get AI response on user input", name);
            return StringUtils.EMPTY;
        }

        var response = results.stream()
                .filter(result -> result.getAuthorRole() == AuthorRole.ASSISTANT && result.getContent() != null)
                .map(ChatMessageContent::getContent)
                .collect(Collectors.joining(" "));

        log.trace("[{}] AI response on user input: {}", name, response);

        return response;
    }
}
