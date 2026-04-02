CREATE TABLE users
(
    id                     UUID                  NOT NULL,
    email                  VARCHAR(255) NOT NULL,
    name                   VARCHAR(255) NOT NULL,
    password               VARCHAR(255),
    website                VARCHAR(255),
    is_email_public        BOOLEAN DEFAULT FALSE NOT NULL,
    confirmation_token     VARCHAR(255),
    confirmed_at           TIMESTAMP WITHOUT TIME ZONE,
    confirmation_sent_at   TIMESTAMP WITHOUT TIME ZONE,
    password_reset_token   VARCHAR(255),
    password_reset_sent_at TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_users PRIMARY KEY (id)
);

ALTER TABLE users
    ADD CONSTRAINT email_users_uk UNIQUE (email);

ALTER TABLE users
    ADD CONSTRAINT name_users_uk UNIQUE (name);