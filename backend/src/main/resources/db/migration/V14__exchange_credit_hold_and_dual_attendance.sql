-- Bos DB'de tablo Hibernate ile olustugu icin migration'lar tabloyu opsiyonel patch gibi davranir.
DO $migration$
BEGIN
  IF EXISTS (
    SELECT 1 FROM information_schema.tables
    WHERE table_schema = current_schema() AND table_name = 'exchange_requests'
  ) THEN
    ALTER TABLE exchange_requests
      ADD COLUMN IF NOT EXISTS requester_credit_held BOOLEAN NOT NULL DEFAULT FALSE;

    ALTER TABLE exchange_requests
      ADD COLUMN IF NOT EXISTS owner_attendance_ack_at TIMESTAMP WITH TIME ZONE;

    ALTER TABLE exchange_requests
      ADD COLUMN IF NOT EXISTS started_prompt_sent BOOLEAN NOT NULL DEFAULT FALSE;
  END IF;
END
$migration$;
