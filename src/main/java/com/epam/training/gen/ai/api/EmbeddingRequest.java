package com.epam.training.gen.ai.api;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@EqualsAndHashCode(callSuper = true)
public class EmbeddingRequest extends ApiRequest {

    private long id;
}
