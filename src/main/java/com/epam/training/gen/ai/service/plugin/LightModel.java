package com.epam.training.gen.ai.service.plugin;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@EqualsAndHashCode
public class LightModel {

    private final int id;
    private final String lightType;
    private boolean isOn;

    public LightModel(int id, String lightType) {
        this.id = id;
        this.lightType = lightType;
    }
}
