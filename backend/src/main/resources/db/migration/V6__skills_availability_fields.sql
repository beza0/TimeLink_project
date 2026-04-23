DO $migration$
BEGIN
  IF EXISTS (
    SELECT 1 FROM information_schema.tables
    WHERE table_schema = current_schema() AND table_name = 'skills'
  ) THEN
    ALTER TABLE skills
        ADD COLUMN IF NOT EXISTS session_types VARCHAR(120),
        ADD COLUMN IF NOT EXISTS in_person_location VARCHAR(120),
        ADD COLUMN IF NOT EXISTS available_days VARCHAR(200),
        ADD COLUMN IF NOT EXISTS available_from VARCHAR(5),
        ADD COLUMN IF NOT EXISTS available_until VARCHAR(5);
  END IF;
END
$migration$;
