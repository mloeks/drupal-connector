package com.mloeks.drupalconnector;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mloeks.drupalconnector.domain.ItemCandidate;
import com.mloeks.drupalconnector.domain.Node;
import com.mloeks.drupalconnector.domain.Response;

import java.util.*;

public class DrupalConnector {

    public Response read(String json) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper()
                .configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
        return mapper.readValue(json, Response.class);
    }

    public static List<ItemCandidate> resolve(final Response response) {
        return response.data().stream()
                .map(node -> new ItemCandidate(
                        node.id(), node.type(), resolveRecursive(node, new ArrayList<>(), response.included())))
                .toList();
    }

    public static Map<String, Object> resolveRecursive(
            final Node node, final List<String> keyPrefixes, final Map<UUID, Node> relatedNodesLookup
    ) {
        Map<String, Object> attrs = getTopLevelAttributes(node, keyPrefixes);
        attrs.putAll(getRelatedNodesAttributes(node, keyPrefixes, relatedNodesLookup));
        return attrs;
    }

    private static Map<String, Object> getTopLevelAttributes(Node node, List<String> keyPrefixes) {
        return new HashMap<>(node.attributes()
                .entrySet().stream()
                .collect(HashMap::new, (m,entry) ->
                        m.put(toDotSeparatedString(keyPrefixes, entry.getKey()), entry.getValue()), HashMap::putAll));
    }

    private static Map<String, Object> getRelatedNodesAttributes(
            Node node, List<String> keyPrefixes, Map<UUID, Node> relatedNodesLookup
    ) {
        if (node.relationships() == null) {
            return Collections.emptyMap();
        }

        Map<String, Object> attrs = new HashMap<>();
        node.relationships().forEach((key, relationship) -> {
            if (relationship.data() != null) {
                relationship.data().forEach(data -> {
                    final List<String> relationshipKeyPrefixes = new ArrayList<>(keyPrefixes);
                    relationshipKeyPrefixes.add(key);

                    attrs.put(toDotSeparatedString(relationshipKeyPrefixes, "id"), data.id());
                    Optional.ofNullable(relatedNodesLookup.get(data.id()))
                            .ifPresent(relatedNode -> attrs.putAll(resolveRecursive(relatedNode, relationshipKeyPrefixes, relatedNodesLookup)));
                });
            }
        });
        return attrs;
    }

    public static String toDotSeparatedString(final List<String> prefixes, final String last) {
        final List<String> withLast = new ArrayList<>(prefixes);
        withLast.add(last);
        return String.join(".", withLast);
    }

}
