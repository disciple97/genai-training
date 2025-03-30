package com.epam.training.gen.ai.service.plugin;

import com.microsoft.semantickernel.semanticfunctions.annotations.DefineKernelFunction;
import com.microsoft.semantickernel.semanticfunctions.annotations.KernelFunctionParameter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
public class LightsPlugin {

    /* Mock data for the lights */
    private final Map<Integer, LightModel> lights;

    public LightsPlugin() {
        lights = Map.of(
                1, new LightModel(1, "Table lamp"),
                2, new LightModel(2, "Porch light"),
                3, new LightModel(3, "Chandelier")
        );
    }

    @DefineKernelFunction(name = "get_lights_state", description = "Gets list of lights and their current state")
    public Collection<LightModel> getLightsState() {
        var lightsState = lights.values();
        log.trace("Lights state: {}", lightsState);
        return lightsState;
    }

    @DefineKernelFunction(name = "get_lights_id_to_type_mapping", description = "Gets mapping light IDs and light types")
    public Map<Integer, String> getLightsIdToTypeMapping() {
        var lightsMapping = lights.values().stream().collect(Collectors.toMap(LightModel::getId, LightModel::getLightType));
        log.trace("Lights mapping: {}", lightsMapping);
        return lightsMapping;
    }

    @DefineKernelFunction(name = "change_light_state", description = "Changes state of a light and return its updated state")
    public LightModel changeLightState(
            @KernelFunctionParameter(name = "id", description = "ID of a light to change") int id,
            @KernelFunctionParameter(name = "isOn", description = "New state of a light") boolean isOn
    ) {
        log.trace("Change {} light state: {}", id, isOn);
        var light = lights.get(id);
        if (light == null) {
            throw new IllegalArgumentException("Light not found");
        }
        light.setOn(isOn);
        log.trace("Updated {} light state: {}", id, light);
        return light;
    }
}
