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

CREATE OR REPLACE FUNCTION prevent_assigned_at_update()
RETURNS TRIGGER AS $$
BEGIN
    -- 'OLD' representa a linha como ela estava ANTES do update.
    -- 'NEW' representa a linha como ela ficará DEPOIS do update.
    -- Usamos 'IS DISTINCT FROM' para tratar corretamente valores NULL.
    IF NEW.assigned_at IS DISTINCT FROM OLD.assigned_at THEN
        -- Se o valor de 'assigned_at' estiver sendo alterado, lançamos um erro.
        RAISE EXCEPTION 'Column "assigned_at" is not updatable';
    END IF;

    -- Se a coluna 'assigned_at' não foi alterada, a operação é permitida.
    -- Retornamos 'NEW' para que o update das outras colunas possa continuar.
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TABLE users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    email VARCHAR(100) NOT NULL,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,

    password VARCHAR(255) NOT NULL,

    username VARCHAR(100) NOT NULL,
    profile_picture_url TEXT,
    status_message VARCHAR(255),
    account_status vARCHAR(30) NOT NULL, -- online, offline, busy

    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE nodes (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(21) NOT NULL,
    description TEXT,
    creator_id UUID REFERENCES users(id) ON DELETE SET NULL,
    parent_node_id UUID REFERENCES nodes(id) ON DELETE SET NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- Tabela de Posts (Threads)
CREATE TABLE threads (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    title VARCHAR(255) NOT NULL,
    content TEXT,
    creator_id UUID REFERENCES users(id) ON DELETE SET NULL,
    node_id UUID REFERENCES nodes(id) ON DELETE SET NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- Tabela de Respostas (Replies)
-- Permite respostas aninhadas com 'parent_reply_id'.
CREATE TABLE replies (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    content TEXT NOT NULL,
    creator_id UUID REFERENCES users(id) ON DELETE SET NULL,
    thread_id UUID REFERENCES threads(id) ON DELETE SET NULL,
    parent_reply_id UUID REFERENCES replies(id) ON DELETE SET NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- Tabela de Junção para Moderadores (Muitos-para-Muitos)
CREATE TABLE node_moderators (
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    node_id UUID NOT NULL REFERENCES nodes(id) ON DELETE CASCADE,
    assigned_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    PRIMARY KEY (user_id, node_id)
);

-- A chave primária seria (user_id, thread_id) para votos em threads e (user_id, reply_id) para votos em replies.
-- Como uma das colunas sempre será nula, a forma correta de garantir a unicidade no Postgres é com duas constraints
-- UNIQUE parciais.
CREATE TABLE simple_votes (
    -- Um id é redundante aqui, mas facilita a manutenção
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    thread_id UUID REFERENCES threads(id) ON DELETE CASCADE,
    reply_id UUID REFERENCES replies(id) ON DELETE CASCADE,
    -- 1 para upvote, -1 para downvote
    direction SMALLINT NOT NULL CHECK (direction IN (1, -1)),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    -- Garante que o voto é ou para uma thread ou para uma reply, mas não ambos.
    CONSTRAINT check_vote_target CHECK (
        (thread_id IS NOT NULL AND reply_id IS NULL) OR
        (thread_id IS NULL AND reply_id IS NOT NULL)
    ),

    -- Garante que um usuário só pode votar uma vez em uma mesma thread.
    CONSTRAINT unique_user_vote_on_thread UNIQUE (user_id, thread_id),

    -- Garante que um usuário só pode votar uma vez em uma mesma reply.
    CONSTRAINT unique_user_vote_on_reply UNIQUE (user_id, reply_id)
);

CREATE TABLE node_subscriptions (
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    node_id UUID NOT NULL REFERENCES nodes(id) ON DELETE CASCADE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    -- Chave primária composta para garantir uma única inscrição por usuário/node.
    PRIMARY KEY (user_id, node_id)
);

CREATE TABLE tags (
    -- UUID não é necessário aqui, um ID sequencial é mais simples e eficiente
    id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name VARCHAR(50) UNIQUE NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE thread_tags (
    thread_id UUID NOT NULL REFERENCES threads(id) ON DELETE CASCADE,
    tag_id BIGINT NOT NULL REFERENCES tags(id) ON DELETE CASCADE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    PRIMARY KEY (thread_id, tag_id)
);

CREATE INDEX idx_nodes_creator_id ON nodes(creator_id);
CREATE INDEX idx_nodes_parent_node_id ON nodes(parent_node_id);
CREATE INDEX idx_nodes_name ON nodes(name);
CREATE INDEX idx_threads_creator_id ON threads(creator_id);
CREATE INDEX idx_threads_node_id_created_at ON threads(node_id, created_at DESC);
CREATE INDEX idx_replies_creator_id ON replies(creator_id);
CREATE INDEX idx_replies_parent_reply_id ON replies(parent_reply_id);
CREATE INDEX idx_replies_thread_id_created_at ON replies(thread_id, created_at ASC);
CREATE INDEX idx_node_moderators_node_id ON node_moderators(node_id);
CREATE INDEX idx_simple_votes_thread_id ON simple_votes(thread_id);
CREATE INDEX idx_simple_votes_reply_id ON simple_votes(reply_id);
CREATE INDEX idx_node_subscriptions_node_id ON node_subscriptions(node_id);
CREATE INDEX idx_thread_tags_tag_id ON thread_tags(tag_id);
CREATE INDEX idx_score_user_id ON score(user_id);
CREATE INDEX idx_score_value ON score(value DESC); -- Para buscar rapidamente os usuários com maior reputação


CREATE TRIGGER set_timestamp_users BEFORE UPDATE ON users FOR EACH ROW EXECUTE FUNCTION trigger_set_timestamp();
CREATE TRIGGER trg_users_prevent_created_at_update BEFORE UPDATE ON users FOR EACH ROW EXECUTE FUNCTION prevent_created_at_update();

CREATE TRIGGER set_timestamp_score BEFORE UPDATE ON score FOR EACH ROW EXECUTE FUNCTION trigger_set_timestamp();
CREATE TRIGGER trg_score_prevent_created_at_update BEFORE UPDATE ON score FOR EACH ROW EXECUTE FUNCTION prevent_created_at_update();

CREATE TRIGGER set_timestamp_nodes BEFORE UPDATE ON nodes FOR EACH ROW EXECUTE FUNCTION trigger_set_timestamp();
CREATE TRIGGER trg_nodes_prevent_created_at_update BEFORE UPDATE ON nodes FOR EACH ROW EXECUTE FUNCTION prevent_created_at_update();

CREATE TRIGGER set_timestamp_threads BEFORE UPDATE ON threads FOR EACH ROW EXECUTE FUNCTION trigger_set_timestamp();
CREATE TRIGGER trg_threads_prevent_created_at_update BEFORE UPDATE ON threads FOR EACH ROW EXECUTE FUNCTION prevent_created_at_update();

CREATE TRIGGER set_timestamp_replies BEFORE UPDATE ON replies FOR EACH ROW EXECUTE FUNCTION trigger_set_timestamp();
CREATE TRIGGER trg_replies_prevent_created_at_update BEFORE UPDATE ON replies FOR EACH ROW EXECUTE FUNCTION prevent_created_at_update();

CREATE TRIGGER set_timestamp_node_moderators BEFORE UPDATE ON node_moderators FOR EACH ROW EXECUTE FUNCTION trigger_set_timestamp();
CREATE TRIGGER trg_node_moderators_prevent_created_at_update BEFORE UPDATE ON node_moderators FOR EACH ROW EXECUTE FUNCTION prevent_created_at_update();

CREATE TRIGGER set_timestamp_simple_votes BEFORE UPDATE ON simple_votes FOR EACH ROW EXECUTE FUNCTION trigger_set_timestamp();
CREATE TRIGGER trg_simple_votes_prevent_created_at_update BEFORE UPDATE ON simple_votes FOR EACH ROW EXECUTE FUNCTION prevent_created_at_update();

CREATE TRIGGER set_timestamp_node_subscriptions BEFORE UPDATE ON node_subscriptions FOR EACH ROW EXECUTE FUNCTION trigger_set_timestamp();
CREATE TRIGGER trg_node_subscriptions_prevent_created_at_update BEFORE UPDATE ON node_subscriptions FOR EACH ROW EXECUTE FUNCTION prevent_created_at_update();

CREATE TRIGGER set_timestamp_tags BEFORE UPDATE ON tags FOR EACH ROW EXECUTE FUNCTION trigger_set_timestamp();
CREATE TRIGGER trg_tags_prevent_created_at_update BEFORE UPDATE ON tags FOR EACH ROW EXECUTE FUNCTION prevent_created_at_update();

CREATE TRIGGER set_timestamp_thread_tags BEFORE UPDATE ON thread_tags FOR EACH ROW EXECUTE FUNCTION trigger_set_timestamp();
CREATE TRIGGER trg_thread_tags_prevent_created_at_update BEFORE UPDATE ON thread_tags FOR EACH ROW EXECUTE FUNCTION prevent_created_at_update();

CREATE TRIGGER trg_node_moderators_prevent_assigned_at_update BEFORE UPDATE ON node_moderators FOR EACH ROW EXECUTE FUNCTION prevent_assigned_at_update();