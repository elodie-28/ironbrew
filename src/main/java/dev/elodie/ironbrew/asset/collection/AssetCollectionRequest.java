package dev.elodie.ironbrew.asset.collection;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

/**
 * DTO for {@link AssetCollection}
 */
@Getter @Setter
public class AssetCollectionRequest implements Serializable {
    @NotBlank(message = "Name is required.")
    private String name;
    private String canonicalName;
    private String color;
    private List<String> replaces;
    private List<String> enhances;
    private String summary;
    private String description;
}