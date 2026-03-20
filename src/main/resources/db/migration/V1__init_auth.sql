-- V1__init_auth.sql
-- Jour 3 : Authentification et rôles ClubGIT

-- Enum des rôles
CREATE TYPE role_enum AS ENUM ('BUREAU', 'CHEF_COMMISSION', 'MEMBRE');

-- Enum du statut de compte
CREATE TYPE account_status_enum AS ENUM ('ACTIF', 'SUSPENDU', 'BLOQUE');

-- Table principale des membres
CREATE TABLE membres (
    id                  SERIAL PRIMARY KEY,
    nom                 VARCHAR(100)  NOT NULL,
    prenom              VARCHAR(100)  NOT NULL,
    email               VARCHAR(255)  NOT NULL UNIQUE,
    password_hash       VARCHAR(255)  NOT NULL,
    role                role_enum     NOT NULL DEFAULT 'MEMBRE',
    statut              account_status_enum NOT NULL DEFAULT 'ACTIF',
    solde_points        INTEGER       NOT NULL DEFAULT 0,
    commission_id       SERIAL,                        -- FK ajoutée en Jour 4
    date_inscription    TIMESTAMP     NOT NULL DEFAULT now(),
    derniere_connexion  TIMESTAMP,
    created_at          TIMESTAMP     NOT NULL DEFAULT now(),
    updated_at          TIMESTAMP     NOT NULL DEFAULT now()
);

-- Index pour les recherches fréquentes
CREATE INDEX idx_membres_email  ON membres(email);
CREATE INDEX idx_membres_role   ON membres(role);
CREATE INDEX idx_membres_statut ON membres(statut);

-- Table refresh tokens (rotation des tokens)
CREATE TABLE refresh_tokens (
    id          SERIAL PRIMARY KEY,
    membre_id   SERIAL        NOT NULL REFERENCES membres(id) ON DELETE CASCADE,
    token       VARCHAR(512) NOT NULL UNIQUE,
    expires_at  TIMESTAMP   NOT NULL,
    revoked     BOOLEAN     NOT NULL DEFAULT false,
    created_at  TIMESTAMP   NOT NULL DEFAULT now()
);

CREATE INDEX idx_refresh_tokens_membre ON refresh_tokens(membre_id);
CREATE INDEX idx_refresh_tokens_token  ON refresh_tokens(token);

-- Trigger : mise à jour automatique de updated_at
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = now();
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_membres_updated_at
    BEFORE UPDATE ON membres
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- Compte Bureau initial (mot de passe : Bureau@ClubGIT2026 — à changer en prod)
-- Hash BCrypt strength 12 généré hors ligne
INSERT INTO membres (nom, prenom, email, password_hash, role, statut)
VALUES (
    'Bureau',
    'ClubGIT',
    'bureau@clubgit.enspd.cm',
    '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMlJVjzJ1Rb2pX8w5sDlbXhV.q',
    'BUREAU',
    'ACTIF'
);
