package com.epam.training.gen.ai.api;

import java.util.List;

public record EmbeddingResponse(String input, List<Float> embedding) {
}
