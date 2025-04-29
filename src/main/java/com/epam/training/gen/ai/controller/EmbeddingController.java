package com.epam.training.gen.ai.controller;

import com.epam.training.gen.ai.api.ApiRequest;
import com.epam.training.gen.ai.api.EmbeddingRequest;
import com.epam.training.gen.ai.api.EmbeddingResponse;
import com.epam.training.gen.ai.api.EmbeddingSimilarityResponse;
import com.epam.training.gen.ai.service.EmbeddingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
@RestController
@RequestMapping("/api/embedding")
public class EmbeddingController {

    private final EmbeddingService embeddingService;

    public EmbeddingController(EmbeddingService embeddingService) {
        this.embeddingService = embeddingService;
    }

    @PostMapping(value = "/get", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public EmbeddingResponse getEmbedding(@RequestBody ApiRequest apiRequest) {
        log.trace("Input API get request: {}", apiRequest);
        var input = apiRequest.getInput();
        var embedding = embeddingService.getEmbedding(input);
        var embeddingResponse = new EmbeddingResponse(input, embedding);
        log.trace("Retrieved number of embeddings for input ({}): {}", input, embedding.size());
        return embeddingResponse;
    }

    @PostMapping(value = "/save", consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> save(@RequestBody EmbeddingRequest embeddingRequest) {
        log.trace("Input API save request: {}", embeddingRequest);
        embeddingService.save(embeddingRequest.getId(), embeddingRequest.getInput());
        log.trace("Operation has been completed");
        return ResponseEntity.accepted().build();
    }

    @PostMapping(value = "/search", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public EmbeddingSimilarityResponse search(@RequestBody ApiRequest apiRequest) {
        log.trace("Input API search request: {}", apiRequest);
        var response = embeddingService.search(apiRequest.getInput());
        log.trace("Search response: {}", response);
        return response;
    }
}
