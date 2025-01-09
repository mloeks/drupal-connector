package com.mloeks.drupalconnector;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mloeks.drupalconnector.domain.ItemCandidate;
import com.mloeks.drupalconnector.domain.Node;
import com.mloeks.drupalconnector.domain.Response;

import java.util.List;

public class DrupalConnector {

    public Response read(String json) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper()
                .configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
        return mapper.readValue(json, Response.class);
    }

    public static List<ItemCandidate> resolve(final Response response) {
        return response.data().stream()
                .map(DrupalConnector::toCandidateFlat)
                .toList();
    }

    public static ItemCandidate toCandidateFlat(final Node node) {
        return new ItemCandidate(node.id(), node.type(), node.attributes());
    }

}
