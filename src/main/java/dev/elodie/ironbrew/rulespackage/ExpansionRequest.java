package dev.elodie.ironbrew.rulespackage;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.URL;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * DTO for {@link Expansion}
 */
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
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
}