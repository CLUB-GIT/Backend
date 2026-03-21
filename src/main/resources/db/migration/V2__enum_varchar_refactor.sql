-- V2__enum_varchar_refactor.sql
-- Remplacement des types enum natifs PostgreSQL par VARCHAR + CHECK constraint.
--
-- Raisons :
--   1. Hibernate gère nativement VARCHAR avec @Enumerated(EnumType.STRING)
--   2. ALTER TYPE pour ajouter/supprimer une valeur est non-transactionnel en PG
--   3. La validation Java (@Valid + @Enumerated) est suffisante pour ce projet
--   4. Les migrations futures (jours 5, 6, 9) seront plus simples

-- ── Table membres ─────────────────────────────────────────────────────────────

-- 1. Ajouter les colonnes VARCHAR temporaires
ALTER TABLE membres ADD COLUMN role_new   VARCHAR(30);
ALTER TABLE membres ADD COLUMN statut_new VARCHAR(30);

-- 2. Copier les données existantes (cast explicite depuis le type enum)
UPDATE membres SET role_new   = role::TEXT;
UPDATE membres SET statut_new = statut::TEXT;

-- 3. Supprimer les anciennes colonnes typées enum
ALTER TABLE membres DROP COLUMN role;
ALTER TABLE membres DROP COLUMN statut;

-- 4. Renommer les colonnes temporaires
ALTER TABLE membres RENAME COLUMN role_new   TO role;
ALTER TABLE membres RENAME COLUMN statut_new TO statut;

-- 5. Ajouter les contraintes NOT NULL et CHECK (remplace la validation enum)
ALTER TABLE membres
    ALTER COLUMN role   SET NOT NULL,
    ALTER COLUMN statut SET NOT NULL;

ALTER TABLE membres
    ADD CONSTRAINT chk_membres_role
        CHECK (role IN ('BUREAU', 'CHEF_COMMISSION', 'MEMBRE')),
    ADD CONSTRAINT chk_membres_statut
        CHECK (statut IN ('ACTIF', 'SUSPENDU', 'BLOQUE'));

-- 6. Restaurer les valeurs par défaut
ALTER TABLE membres ALTER COLUMN statut SET DEFAULT 'ACTIF';

-- ── Suppression des types natifs devenus inutiles ─────────────────────────────
-- (seulement si aucune autre table ne les utilise)
DROP TYPE IF EXISTS role_enum;
DROP TYPE IF EXISTS account_status_enum;
