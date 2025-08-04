CREATE EXTENSION IF NOT EXISTS unaccent;

CREATE OR REPLACE FUNCTION prevent_created_at_update()
RETURNS TRIGGER AS $$
BEGIN
    -- 'OLD' representa a linha como ela estava ANTES do update.
    -- 'NEW' representa a linha como ela ficará DEPOIS do update.
    -- Usamos 'IS DISTINCT FROM' para tratar corretamente valores NULL.
    IF NEW.created_at IS DISTINCT FROM OLD.created_at THEN
        -- Se o valor de 'created_at' estiver sendo alterado, lançamos um erro.
        RAISE EXCEPTION 'Column "created_at" is not updatable';
    END IF;

    -- Se a coluna 'created_at' não foi alterada, a operação é permitida.
    -- Retornamos 'NEW' para que o update das outras colunas possa continuar.
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION trigger_set_timestamp()
RETURNS TRIGGER AS $$
BEGIN
  -- Define o campo 'updated_at' da nova versão da linha ('NEW')
  -- para o tempo atual antes de o update ser salvo.
  NEW.updated_at = NOW();
  RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TABLE users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    email VARCHAR(100) NOT NULL,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,

    password VARCHAR(255) NOT NULL,

    display_name VARCHAR(100) NOT NULL,
    profile_picture_url TEXT,
    status_message VARCHAR(255),
    account_status vARCHAR(30) NOT NULL, -- online, offline, busy

    last_seen TIMESTAMPTZ,

    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TRIGGER trg_users_prevent_created_at_update BEFORE UPDATE ON users FOR EACH ROW EXECUTE FUNCTION prevent_created_at_update();
CREATE TRIGGER set_timestamp_users BEFORE UPDATE ON users FOR EACH ROW EXECUTE FUNCTION trigger_set_timestamp();