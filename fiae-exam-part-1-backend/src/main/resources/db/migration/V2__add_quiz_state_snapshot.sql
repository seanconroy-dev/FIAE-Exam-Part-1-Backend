CREATE TABLE
    quiz_state (
        id BIGSERIAL PRIMARY KEY,
        user_id BIGINT NOT NULL REFERENCES whitelist_user(id),
        module_key VARCHAR(255) NOT NULL,
        module_name VARCHAR(255),
        queue_slugs JSONB NOT NULL,
        current_index INTEGER NOT NULL,
        results_by_slug JSONB NOT NULL,
        completed BOOLEAN NOT NULL DEFAULT FALSE,
        revision BIGINT NOT NULL,
        started_at TIMESTAMP NOT NULL,
        updated_at TIMESTAMP NOT NULL,
        UNIQUE (user_id, module_key)
    );
