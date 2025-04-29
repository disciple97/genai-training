package com.epam.training.gen.ai.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties("client.embedding")
public class EmbeddingClientConfig {

    private String vectorStorageHost;
    private int vectorStoragePort;
    private String deploymentName;
    private int vectorSize;
}
