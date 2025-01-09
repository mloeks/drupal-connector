package com.mloeks.drupalconnector.domain;

import java.util.Map;
import java.util.UUID;

public record ItemCandidate(
        UUID id,
        String type,
        Map<String, Object> attributes
) {
}
