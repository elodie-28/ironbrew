package dev.elodie.ironbrew.rulespackage;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.UUID;

/**
 * DTO for {@link Expansion}
 */
public record ExpansionResponse (
        UUID id,
        String title,
        String dataswornId,
        String description,
        LocalDate releaseDate,
        String url,
        String license,
        String ruleset
) implements Serializable {
}