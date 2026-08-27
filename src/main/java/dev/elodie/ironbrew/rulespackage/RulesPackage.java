package dev.elodie.ironbrew.rulespackage;

import dev.elodie.ironbrew.asset.collection.AssetCollection;
import dev.elodie.ironbrew.security.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(
        name = "rules_packages",
        check = {
                @CheckConstraint(
                        name = "rules_packages_expansion_ruleset_check",
                        constraint = "(type = 'ruleset' AND ruleset IS NULL) OR (type = 'expansion' AND ruleset IS NOT NULL)"
                )
        }
)
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "type")
@Getter @Setter @NoArgsConstructor
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

    @OneToMany(fetch = FetchType.LAZY)
    private List<AssetCollection> assetCollections =  new ArrayList<>();
}
