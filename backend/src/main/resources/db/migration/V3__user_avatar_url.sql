DO $migration$
BEGIN
  IF EXISTS (
    SELECT 1 FROM information_schema.tables
    WHERE table_schema = current_schema() AND table_name = 'users'
  ) THEN
    ALTER TABLE users ADD COLUMN IF NOT EXISTS avatar_url TEXT;
  END IF;
END
$migration$;
