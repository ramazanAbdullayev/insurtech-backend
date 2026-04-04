CREATE TABLE IF NOT EXISTS claim
(
    id                   UUID         PRIMARY KEY,
    claim_number         UUID         NOT NULL,
    user_id              UUID         NOT NULL,
    accident_type        VARCHAR(255) NOT NULL,
    occurred_at          TIMESTAMPTZ  NOT NULL,
    other_party_involved BOOLEAN      NOT NULL,
    status               VARCHAR(20)  NOT NULL,
    location             VARCHAR(255) NOT NULL,
    description          TEXT,
    created_at           TIMESTAMPTZ  NOT NULL,
    updated_at           TIMESTAMPTZ,

    CONSTRAINT fk_claim_user_id FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS claim_user_id_idx      ON claim (user_id);
CREATE INDEX IF NOT EXISTS claim_claim_number_idx ON claim (claim_number);

-- ================================================================================================

CREATE TABLE IF NOT EXISTS claim_file
(
    id                 UUID         PRIMARY KEY,
    claim_id           UUID         NOT NULL,
    type               VARCHAR(20)  NOT NULL,
    file_key           VARCHAR(255) NOT NULL,
    original_file_name VARCHAR(255) NOT NULL,
    size               BIGINT       NOT NULL ,
    content_type       VARCHAR(255) NOT NULL,
    status             VARCHAR(20)  NOT NULL,
    created_at         TIMESTAMPTZ  NOT NULL,
    uploaded_at        TIMESTAMPTZ,
    updated_at         TIMESTAMPTZ,

    CONSTRAINT fk_claim_file_claim_id FOREIGN KEY (claim_id) REFERENCES claim (id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS claim_file_claim_id_idx ON claim_file (claim_id);

-- ================================================================================================

CREATE TABLE IF NOT EXISTS claim_estimation
(
    id             UUID           PRIMARY KEY,
    claim_id       UUID           NOT NULL UNIQUE,
    ai_confidence  FLOAT          NOT NULL,
    estimated_cost DECIMAL(15, 2) NOT NULL,
    raw_response   TEXT,
    status         VARCHAR(20)    NOT NULL ,
    created_at     TIMESTAMPTZ    NOT NULL,

    CONSTRAINT fk_claim_estimation_claim_id FOREIGN KEY (claim_id) REFERENCES claim (id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS claim_estimation_claim_id_idx ON claim_file (claim_id)