package com.epam.training.gen.ai.config;

/**
 * Deployment configuration.
 *
 * @param name  Deployment name
 * @param model Deployment model
 */
public record DeploymentConfig(String name, String model) {
}
