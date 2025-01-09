package com.mloeks.drupalconnector;

import com.mloeks.drupalconnector.domain.ItemCandidate;
import com.mloeks.drupalconnector.domain.Response;
import org.junit.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

public class DrupalConnectorTest {

    @Test
    public void maps_as_expected() throws IOException, URISyntaxException {
        // GIVEN
        String json = Files.readString(Paths.get(getClass().getClassLoader().getResource(
                "umami_articles_with_includes.json").toURI()));

        // WHEN
        Response resp = new DrupalConnector().read(json);

        // THEN
        assertThat(resp.data()).hasSize(2);

        assertThat(resp.included()).hasSize(6);
        assertThat(resp.included().values()).extracting("type")
                .containsExactlyInAnyOrder(
                        "media--image",
                        "media_type--media_type",
                        "user--user",
                        "user--user",
                        "media--image",
                        "user--user"
                );
    }

    @Test
    public void resolves_relationships() throws IOException, URISyntaxException {
        // GIVEN
        String json = Files.readString(Paths.get(getClass().getClassLoader().getResource(
                "umami_articles_with_includes.json").toURI()));
        Response resp = new DrupalConnector().read(json);

        // WHEN
        List<ItemCandidate> candidates = DrupalConnector.resolve(resp);

        // THEN
        assertThat(candidates)
                .hasSize(2)
                .extracting("type", "id")
                .containsExactly(
                        tuple("node--article", UUID.fromString("f0f7c2cf-b860-4969-9594-63086a37766a")),
                        tuple("node--article", UUID.fromString("14650b8c-2af3-4e77-88f3-ea6a57e04a89"))
                );

        assertThat(candidates).map(ItemCandidate::attributes)
                .allSatisfy(attrs -> assertThat(attrs).containsKeys(
                        // top level attributes
                        "drupal_internal__nid", "drupal_internal__vid", "langcode", "revision_timestamp",
                        "created", "changed", "path", "body",
                        // from related nodes, connected with a dot
                        "revision_uid.id",  // do we need to put this?
                        "revision_uid.display_name",
                        "uid.id",
                        "uid.display_name",
                        "node_type.id",  // not resolved in includes, still put top level relationship's ID?
                        "field_media_image.id",
                        "field_media_image.drupal_internal__mid",
                        "field_media_image.path",
                        "field_media_image.created",
                        "field_media_image.changed",

                        // nested relationship
                        "field_media_image.bundle.id",
                        "field_media_image.bundle.label"
                ));

        assertThat(candidates.get(0).attributes()).containsAllEntriesOf(Map.of(
                "promote", true,
                "sticky", false,
                "moderation_state", "published"
        ));
        assertThat(candidates.get(0).attributes()).containsAllEntriesOf(Map.of(
                "revision_uid.id", UUID.fromString("3a577e34-2a6a-4ed1-acb3-de923de6a313"),
                "revision_uid.display_name", "Umami",
                "uid.display_name", "Umami",

                "field_media_image.id", UUID.fromString("dd4fe495-ef6d-47ae-a056-d5a2f4bb6d25"),
                "field_media_image.langcode", "en",
                "field_media_image.content_translation_source", "und",
                "field_media_image.content_translation_outdated", false,

                // nested relationship
                "field_media_image.bundle.id", UUID.fromString("7857f44f-ae5b-42cf-b35d-cd7d900423eb"),
                "field_media_image.bundle.label", "Image"
        ));

        assertThat(candidates.get(0).attributes().get("field_media_image.path"))
                .extracting("alias", "pid", "langcode")
                .containsExactly(null, null, "en");
    }
}
