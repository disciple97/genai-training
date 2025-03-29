package com.epam.training.gen.ai.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * OpenAI client configuration properties.
 */
@Getter
@Setter
@Component
@ConfigurationProperties("client.openai")
public class OpenAiClientConfig {

    /**
     * API key
     */
    private String apiKey;

    /**
     * API endpoint
     */
    private String apiEndpoint;

    /**
     * Deployment (model) name
     */
    private String deploymentName;

    /**
     * Model execution temperature
     */
    private double executionTemperature;

    /**
     * Initial system prompt
     */
    private String systemPrompt;

    /**
     * List of supported deployments
     */
    private List<DeploymentConfig> deployments;
}
