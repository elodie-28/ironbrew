package dev.elodie.ironbrew.rulespackage;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.URL;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.UUID;

/**
 * DTO for {@link Expansion}
 */
public class ExpansionRequest implements Serializable {
    @NotBlank(message = "Title is required.")
    private String title;
    private String dataswornId;
    private String description;
    @NotNull(message = "Release date is required.")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate releaseDate;
    @URL(message = "URL must be valid.")
    @NotBlank(message = "URL is required.")
    private String url;
    private String license;
    @NotBlank(message = "Ruleset is required.")
    private String ruleset;

    public ExpansionRequest(
            String title,
            String dataswornId,
            String description,
            LocalDate releaseDate,
            String url,
            String license,
            String ruleset
    ) {
        this.title = title;
        this.dataswornId = dataswornId;
        this.description = description;
        this.releaseDate = releaseDate;
        this.url = url;
        this.license = license;
        this.ruleset = ruleset;
    }

    public ExpansionRequest() {}

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