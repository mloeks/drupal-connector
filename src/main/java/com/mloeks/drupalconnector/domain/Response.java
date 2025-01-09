package com.mloeks.drupalconnector.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.mloeks.drupalconnector.deserialization.NodeByIdDeserializer;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Response(
        List<Node> data,
        @JsonDeserialize(using = NodeByIdDeserializer.class)
        Map<UUID, Node> included
) {
}
