package com.mloeks.drupalconnector.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Map;
import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Node(
        String type,
        UUID id,
        Map<String, Object> attributes,
        Map<String, Relationship> relationships
) {
}
