package com.mloeks.drupalconnector.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Collection;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Relationship(
        Collection<Data> data
) {
}
