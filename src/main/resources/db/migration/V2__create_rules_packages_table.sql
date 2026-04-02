CREATE TABLE rules_packages
(
    id           UUID NOT NULL,
    type         VARCHAR(31),
    title        VARCHAR(255) NOT NULL,
    datasworn_id VARCHAR(255),
    description  TEXT,
    release_date date NOT NULL,
    url          VARCHAR(255) NOT NULL,
    license      VARCHAR(255),
    owner_id     UUID,
    ruleset      VARCHAR(255),
    CONSTRAINT pk_rules_packages PRIMARY KEY (id)
);

ALTER TABLE rules_packages
    ADD CONSTRAINT RULES_PACKAGES_OWNER_ID_FK FOREIGN KEY (owner_id) REFERENCES users (id);

ALTER TABLE rules_packages
    ADD CONSTRAINT rules_packages_expansion_ruleset_check CHECK ( (type = 'ruleset' AND ruleset IS NULL) OR (type = 'expansion' AND ruleset IS NOT NULL) );