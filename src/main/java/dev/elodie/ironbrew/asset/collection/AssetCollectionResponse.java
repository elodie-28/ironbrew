package dev.elodie.ironbrew.asset.collection;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

/**
 * DTO for {@link AssetCollection}
 */
public record AssetCollectionResponse(
        UUID id,
        String name,
        String canonicalName,
        String color,
        List<String> replaces,
        List<String> enhances,
        String summary,
        String description
) implements Serializable {
}