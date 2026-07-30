ALTER TABLE expenses DROP CONSTRAINT IF EXISTS expenses_user_id_fkey;
DROP INDEX IF EXISTS idx_expenses_user_id;
DROP INDEX IF EXISTS idx_expenses_category_created_user;

ALTER TABLE expenses DROP COLUMN IF EXISTS user_id;

DROP TABLE IF EXISTS refresh_tokens;
DROP TABLE IF EXISTS users;

CREATE INDEX IF NOT EXISTS idx_expenses_category_created ON expenses (category_id, created_at);
