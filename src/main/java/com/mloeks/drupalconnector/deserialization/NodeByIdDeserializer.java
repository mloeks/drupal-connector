package com.mloeks.drupalconnector.deserialization;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.mloeks.drupalconnector.domain.Node;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Custom deserializer that wraps a list of {@link Node}s into a map with their ID as key.
 */
public class NodeByIdDeserializer extends StdDeserializer<Map<UUID, Node>> {

    public NodeByIdDeserializer() {
        this(null);
    }

    public NodeByIdDeserializer(Class<?> valueClass) {
        super(valueClass);
    }

    @Override
    public Map<UUID, Node> deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        final ArrayNode jsonNodes = p.getCodec().readTree(p);

        final HashMap<UUID, Node> nodesById = new HashMap<>();
        for (JsonNode jsonNode : jsonNodes) {
            final Node deser = ctxt.readTreeAsValue(jsonNode, Node.class);
            nodesById.put(deser.id(), deser);
        }

        return nodesById;
    }
}
