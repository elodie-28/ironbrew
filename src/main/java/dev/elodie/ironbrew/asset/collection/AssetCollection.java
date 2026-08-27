package dev.elodie.ironbrew.asset.collection;

import dev.elodie.ironbrew.rulespackage.RulesPackage;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Entity
@Table(
        name = "asset_collections"
)
@Getter @Setter @NoArgsConstructor
public class AssetCollection {
    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "rules_package_id",
            foreignKey = @ForeignKey(name = "asset_collection_rules_package_id_fk"),
            nullable = false
    )
    private RulesPackage rulesPackage;

    @Basic(optional = false)
    private String name;

    private String canonicalName;

    private String color;

    private List<String> replaces;

    private List<String> enhances;

    @Column(columnDefinition = "text")
    private String summary;

    @Column(columnDefinition = "text")
    private String description;
}
