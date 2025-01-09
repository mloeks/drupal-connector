package com.mloeks.drupalconnector.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Data(String type, UUID id) {
}
