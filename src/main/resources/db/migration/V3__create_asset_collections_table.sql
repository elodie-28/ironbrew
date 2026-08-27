CREATE TABLE asset_collections
(
    id               UUID NOT NULL,
    rules_package_id UUID NOT NULL,
    name             VARCHAR(255) NOT NULL,
    canonical_name   VARCHAR(255),
    color            VARCHAR(255),
    replaces         TEXT[],
    enhances         TEXT[],
    summary          TEXT,
    description      TEXT,
    CONSTRAINT pk_asset_collections PRIMARY KEY (id)
);

ALTER TABLE asset_collections
    ADD CONSTRAINT ASSET_COLLECTION_RULES_PACKAGE_ID_FK FOREIGN KEY (rules_package_id) REFERENCES rules_packages (id) ON DELETE CASCADE;