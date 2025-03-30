package com.epam.training.gen.ai;

import com.azure.ai.openai.OpenAIAsyncClient;
import com.azure.ai.openai.OpenAIClientBuilder;
import com.azure.core.credential.AzureKeyCredential;
import com.epam.training.gen.ai.config.DeploymentConfig;
import com.epam.training.gen.ai.config.OpenAiClientConfig;
import com.epam.training.gen.ai.service.ChatCompletable;
import com.epam.training.gen.ai.service.ChatCompletion;
import com.epam.training.gen.ai.service.plugin.DateTimePlugin;
import com.epam.training.gen.ai.service.plugin.LightsPlugin;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;
import java.util.stream.Collectors;

@Configuration
public class GenAiTrainingApplicationConfig {

    @Bean
    public OpenAIAsyncClient openAIAsyncClient(OpenAiClientConfig openAiClientConfig) {
        return new OpenAIClientBuilder()
                .credential(new AzureKeyCredential(openAiClientConfig.getApiKey()))
                .endpoint(openAiClientConfig.getApiEndpoint())
                .buildAsyncClient();
    }

    @Bean
    public Map<String, ChatCompletable> chatCompletions(
            OpenAiClientConfig openAiClientConfig, OpenAIAsyncClient openAIAsyncClient, DateTimePlugin dateTimePlugin, LightsPlugin lightsPlugin
    ) {
        return openAiClientConfig.getDeployments().stream()
                .collect(Collectors.toMap(
                        DeploymentConfig::name,
                        deploymentConfig -> new ChatCompletion(deploymentConfig, openAIAsyncClient, dateTimePlugin, lightsPlugin)
                ));
    }
}
