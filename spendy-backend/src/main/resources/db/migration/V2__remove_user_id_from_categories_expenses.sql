ALTER TABLE expenses DROP CONSTRAINT IF EXISTS expenses_user_id_fkey;
ALTER TABLE categories DROP CONSTRAINT IF EXISTS categories_user_id_fkey;

DROP INDEX IF EXISTS idx_expenses_user_category_created;
DROP INDEX IF EXISTS idx_categories_user_id;

ALTER TABLE categories DROP COLUMN user_id;
ALTER TABLE expenses DROP COLUMN user_id;

CREATE INDEX idx_expenses_category_created ON expenses (category_id, created_at);
