package dev.elodie.ironbrew.asset.collection;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * DTO for {@link AssetCollection}
 */
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class AssetCollectionRequest implements Serializable {
    @NotBlank(message = "Name is required.")
    private String name;
    private String canonicalName;
    private String color;
    private List<@NotBlank @AssetCollectionIdWildcard String> replaces =  new ArrayList<>();
    private List<@NotBlank @AssetCollectionIdWildcard String> enhances = new ArrayList<>();
    private String summary;
    private String description;
}