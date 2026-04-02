package dev.elodie.ironbrew.rulespackage;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue(value = "expansion")
public class Expansion extends RulesPackage {
    private String ruleset;

    public String getRuleset() {
        return ruleset;
    }

    public void setRuleset(String ruleset) {
        this.ruleset = ruleset;
    }
}
