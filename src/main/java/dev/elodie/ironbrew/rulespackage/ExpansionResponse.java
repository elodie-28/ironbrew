package dev.elodie.ironbrew.rulespackage;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.UUID;

/**
 * DTO for {@link Expansion}
 */
public class ExpansionResponse implements Serializable {
    private UUID id;
    private String title;
    private String dataswornId;
    private String description;
    private LocalDate releaseDate;
    private String url;
    private String license;
    private String ruleset;

    public ExpansionResponse(
            UUID id,
            String title,
            String dataswornId,
            String description,
            LocalDate releaseDate,
            String url,
            String license,
            String ruleset
    ) {
        this.id = id;
        this.title = title;
        this.dataswornId = dataswornId;
        this.description = description;
        this.releaseDate = releaseDate;
        this.url = url;
        this.license = license;
        this.ruleset = ruleset;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDataswornId() {
        return dataswornId;
    }

    public void setDataswornId(String dataswornId) {
        this.dataswornId = dataswornId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(LocalDate releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getLicense() {
        return license;
    }

    public void setLicense(String license) {
        this.license = license;
    }

    public String getRuleset() {
        return ruleset;
    }

    public void setRuleset(String ruleset) {
        this.ruleset = ruleset;
    }
}