CREATE TABLE
    whitelist_user (
        id BIGSERIAL PRIMARY KEY,
        email VARCHAR(255) NOT NULL UNIQUE,
        github_username VARCHAR(255),
        api_key_hash VARCHAR(512) NOT NULL,
        created_at TIMESTAMP NOT NULL DEFAULT NOW (),
        is_active BOOLEAN NOT NULL DEFAULT TRUE
    );

CREATE TABLE
    learning_progress (
        id BIGSERIAL PRIMARY KEY,
        user_id BIGINT NOT NULL REFERENCES whitelist_user(id),
        card_slug VARCHAR(255) NOT NULL,
        times_seen INT NOT NULL DEFAULT 0,
        times_correct INT NOT NULL DEFAULT 0,
        last_seen_at TIMESTAMP,
        UNIQUE (user_id,card_slug)
    );

CREATE TABLE
    quiz_session (
        session_id UUID PRIMARY KEY,
        user_id BIGINT NOT NULL REFERENCES whitelist_user(id),
        module VARCHAR(512),
        started_at TIMESTAMP NOT NULL DEFAULT NOW(),
        completed_at TIMESTAMP,
        score_correct INT,
        score_wrong INT
    );

CREATE TABLE
    quiz_session_card (
        id BIGSERIAL PRIMARY KEY,
        session_id UUID NOT NULL REFERENCES quiz_session(session_id),
        card_slug VARCHAR(255) NOT NULL,
        was_correct BOOLEAN NOT NULL,
        answered_at TIMESTAMP NOT NULL DEFAULT NOW()
    );