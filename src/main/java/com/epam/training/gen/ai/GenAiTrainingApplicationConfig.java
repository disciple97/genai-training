package com.epam.training.gen.ai;

import com.epam.training.gen.ai.config.OpenAiClientConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GenAiTrainingApplicationConfig {

    @Value("${client-openai-key}")
    private String aiKey;

    @Value("${client-openai-endpoint}")
    private String aiEndpoint;

    @Value("${client-openai-deployment-name}")
    private String aiDeploymentName;

    @Value("${execution-temperature}")
    private double executionTemperature;

    @Value("${system-prompt}")
    private String systemPrompt;

    @Bean
    public OpenAiClientConfig openAiClientConfig() {
        return new OpenAiClientConfig(aiKey, aiEndpoint, aiDeploymentName, executionTemperature, systemPrompt);
    }
}
