package dev.elodie.ironbrew.rulespackage;

import dev.elodie.ironbrew.security.user.User;
import jakarta.persistence.*;
import org.hibernate.annotations.Check;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(
        name = "rules_packages"
)
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "type")
@Check(
        name = "rules_packages_expansion_ruleset_check",
        constraints = "(type = 'ruleset' AND ruleset IS NULL) OR (type = 'expansion' AND ruleset IS NOT NULL)"
)
public abstract class RulesPackage {
    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Basic(optional = false)
    private String title;

    private String dataswornId;

    @Column(columnDefinition = "text")
    private String description;

    @Basic(optional = false)
    private LocalDate releaseDate;

    @Basic(optional = false)
    private String url;

    private String license;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "owner_id",
            foreignKey = @ForeignKey(name = "rules_packages_owner_id_fk"),
            nullable = false
    )
    private User owner;

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

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }
}
