DO $migration$
BEGIN
  IF EXISTS (
    SELECT 1 FROM information_schema.tables
    WHERE table_schema = current_schema() AND table_name = 'exchange_requests'
  ) THEN
    ALTER TABLE exchange_requests
        ADD COLUMN IF NOT EXISTS pending_from_owner BOOLEAN NOT NULL DEFAULT false;
  END IF;
END
$migration$;
