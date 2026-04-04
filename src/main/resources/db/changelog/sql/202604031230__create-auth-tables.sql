CREATE TABLE IF NOT EXISTS users
(
    id            UUID         PRIMARY KEY,
    first_name    VARCHAR(255) NOT NULL,
    last_name     VARCHAR(255) NOT NULL,
    email         VARCHAR(320) NOT NULL UNIQUE,
    password_hash VARCHAR(512) NOT NULL,
    status        VARCHAR(20)  NOT NULL,
    created_at    TIMESTAMPTZ  NOT NULL,
    last_login_at TIMESTAMPTZ
);

CREATE INDEX IF NOT EXISTS users_email_idx ON users (email);

-- ============================================================

CREATE TABLE IF NOT EXISTS user_roles
(
    user_id UUID        NOT NULL,
    role    VARCHAR(50) NOT NULL,

    PRIMARY KEY (user_id, role),
    CONSTRAINT fk_user_roles_user_id FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);

-- ============================================================

CREATE TABLE refresh_tokens
(
    id          UUID        PRIMARY KEY,
    token_hash  VARCHAR(64) NOT NULL UNIQUE,
    user_id     UUID        NOT NULL,
    family_id   UUID        NOT NULL,
    status      VARCHAR(20) NOT NULL,
    issued_at   TIMESTAMPTZ NOT NULL,
    expires_at  TIMESTAMPTZ NOT NULL,
    used_at     TIMESTAMPTZ,
    user_agent  VARCHAR(512),
    ip_address  VARCHAR(45),

    CONSTRAINT fk_refresh_tokens_user_id FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS rt_token_hash_idx     ON refresh_tokens (token_hash);
CREATE INDEX IF NOT EXISTS rt_user_id_idx        ON refresh_tokens (user_id);
CREATE INDEX IF NOT EXISTS rt_family_id_idx      ON refresh_tokens (family_id);
CREATE INDEX IF NOT EXISTS rt_active_expires_idx ON refresh_tokens (expires_at) WHERE status = 'ACTIVE';
