package com.epam.training.gen.ai.service;

import com.azure.ai.openai.OpenAIAsyncClient;
import com.azure.ai.openai.models.EmbeddingsOptions;
import com.epam.training.gen.ai.api.EmbeddingSimilarityResponse;
import com.epam.training.gen.ai.config.EmbeddingClientConfig;
import io.qdrant.client.QdrantClient;
import io.qdrant.client.grpc.Collections;
import io.qdrant.client.grpc.Collections.VectorParams;
import io.qdrant.client.grpc.Points.PointStruct;
import io.qdrant.client.grpc.Points.SearchPoints;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

import static io.qdrant.client.PointIdFactory.id;
import static io.qdrant.client.ValueFactory.value;
import static io.qdrant.client.VectorsFactory.vectors;
import static io.qdrant.client.WithPayloadSelectorFactory.enable;
import static java.util.Collections.singletonList;
import static java.util.Collections.singletonMap;

@Slf4j
@Service
public class EmbeddingService {

    private static final String COLLECTION_NAME = "collection";
    private static final String KEY_VALUE = "value";

    private final String deploymentName;
    private final OpenAIAsyncClient openAIAsyncClient;
    private final QdrantClient qdrantClient;

    public EmbeddingService(EmbeddingClientConfig embeddingClientConfig, OpenAIAsyncClient openAIAsyncClient, QdrantClient qdrantClient) {

        this.deploymentName = embeddingClientConfig.getDeploymentName();
        this.openAIAsyncClient = openAIAsyncClient;
        this.qdrantClient = qdrantClient;

        var vectorParams = VectorParams.newBuilder()
                .setSize(1536)
                .setDistance(Collections.Distance.Cosine)
                .build();

        try {
            var response = this.qdrantClient.createCollectionAsync(COLLECTION_NAME, vectorParams).get();
            log.info("Collection creation result: {}", response.getResult());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public List<Float> getEmbedding(String input) {
        var embeddingsOptions = new EmbeddingsOptions(singletonList(input));
        var oEmbeddings = openAIAsyncClient.getEmbeddings(deploymentName, embeddingsOptions).blockOptional();
        if (oEmbeddings.isEmpty()) {
            return java.util.Collections.emptyList();
        }
        var data = oEmbeddings.get().getData();
        if (data.size() != 1) {
            log.error("Invalid number of embedding items: {}", data.size());
            return java.util.Collections.emptyList();
        }
        return data.get(0).getEmbedding();
    }

    public void save(long id, String input) {
        var embedding = getEmbedding(input);
        var pointStruct = PointStruct.newBuilder()
                .setId(id(id))
                .setVectors(vectors(embedding))
                .putAllPayload(singletonMap(KEY_VALUE, value(input)))
                .build();
        try {
            var result = qdrantClient.upsertAsync(COLLECTION_NAME, singletonList(pointStruct)).get();
            log.trace("Collection updated with status: {}", result.getStatus());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public EmbeddingSimilarityResponse search(String input) {
        var embedding = getEmbedding(input);
        var searchPoints = SearchPoints.newBuilder()
                .setCollectionName(COLLECTION_NAME)
                .addAllVector(embedding)
                .setWithPayload(enable(true))
                .setLimit(1)
                .build();
        try {
            return qdrantClient.searchAsync(searchPoints).get().stream()
                    .map(sp -> new EmbeddingSimilarityResponse(
                            sp.getId().getNum(), sp.getScore(), sp.getPayloadMap().get(KEY_VALUE).getStringValue()
                    ))
                    .findFirst()
                    .get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
