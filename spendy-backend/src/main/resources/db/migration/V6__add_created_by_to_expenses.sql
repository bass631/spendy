ALTER TABLE expenses ADD COLUMN IF NOT EXISTS created_by VARCHAR(255);

UPDATE expenses SET created_by = 'unknown' WHERE created_by IS NULL;

ALTER TABLE expenses ALTER COLUMN created_by SET NOT NULL;
