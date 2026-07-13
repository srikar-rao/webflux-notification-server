CREATE TABLE IF NOT EXISTS notifications (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    message TEXT NOT NULL,
    action_url VARCHAR(2048),
    type VARCHAR(100) NOT NULL,
    priority VARCHAR(50) NOT NULL,
    audience_type VARCHAR(50) NOT NULL,
    severity VARCHAR(50) NOT NULL,
    status VARCHAR(50) NOT NULL,
    targets VARCHAR[],
    expires_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_notifications_targets ON notifications USING GIN (targets);

CREATE TABLE IF NOT EXISTS user_notification_states (
    id BIGSERIAL PRIMARY KEY,
    notification_id VARCHAR NOT NULL,
    user_id VARCHAR NOT NULL,
    read_at TIMESTAMPTZ,
    dismissed_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_user_notification_states_user_id ON user_notification_states (user_id);
CREATE INDEX IF NOT EXISTS idx_user_notification_states_notification_id ON user_notification_states (notification_id);
