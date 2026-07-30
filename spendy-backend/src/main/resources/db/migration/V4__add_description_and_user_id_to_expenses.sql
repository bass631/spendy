ALTER TABLE expenses ADD COLUMN IF NOT EXISTS description VARCHAR(1000);

ALTER TABLE expenses ADD COLUMN IF NOT EXISTS user_id UUID REFERENCES users(id) ON DELETE CASCADE;

UPDATE expenses SET user_id = (SELECT id FROM users LIMIT 1) WHERE user_id IS NULL;

ALTER TABLE expenses ALTER COLUMN user_id SET NOT NULL;

CREATE INDEX IF NOT EXISTS idx_expenses_user_id ON expenses (user_id);

CREATE INDEX IF NOT EXISTS idx_expenses_category_created_user ON expenses (category_id, created_at, user_id);

DROP INDEX IF EXISTS idx_expenses_category_created;
