CREATE TABLE IF NOT EXISTS user_blocks (
    id UUID PRIMARY KEY,
    blocker_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    blocked_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT uk_user_blocks_pair UNIQUE (blocker_id, blocked_id),
    CONSTRAINT chk_user_blocks_no_self CHECK (blocker_id <> blocked_id)
);

CREATE INDEX IF NOT EXISTS idx_user_blocks_blocker_id ON user_blocks (blocker_id);
CREATE INDEX IF NOT EXISTS idx_user_blocks_blocked_id ON user_blocks (blocked_id);
