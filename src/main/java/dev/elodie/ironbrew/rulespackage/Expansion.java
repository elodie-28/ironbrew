package dev.elodie.ironbrew.rulespackage;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@DiscriminatorValue(value = "expansion")
@Setter @Getter @NoArgsConstructor
public class Expansion extends RulesPackage {
    private String ruleset;
}
