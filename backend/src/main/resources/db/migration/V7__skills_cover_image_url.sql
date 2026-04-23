DO $migration$
BEGIN
  IF EXISTS (
    SELECT 1 FROM information_schema.tables
    WHERE table_schema = current_schema() AND table_name = 'skills'
  ) THEN
    ALTER TABLE skills
        ADD COLUMN IF NOT EXISTS cover_image_url TEXT;
  END IF;
END
$migration$;
