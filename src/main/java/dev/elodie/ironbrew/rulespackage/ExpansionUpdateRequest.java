package dev.elodie.ironbrew.rulespackage;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
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
public class ExpansionUpdateRequest implements Serializable {
    @NotBlank(message = "Title is required.")
    private String title;
    @Pattern(regexp = "^[a-z][a-z0-9_]*$", message = "Datasworn ID must contain only lowercase letters, digits and _.")
    private String dataswornId;
    private String description;
    @NotNull(message = "Release date is required.")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate releaseDate;
    @URL(message = "URL must be valid.")
    @NotBlank(message = "URL is required.")
    private String url;
    private String license;
}