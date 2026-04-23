-- Oturum zamanı + hatırlatıcı bayrağı (eski volume uyumluluğu)
-- Flyway Hibernate'den önce çalışır; boş DB'de tablo yoksa atlanır (Hibernate sonradan ekler / ExchangeRequestSchemaPatch).
DO $migration$
BEGIN
  IF EXISTS (
    SELECT 1 FROM information_schema.tables
    WHERE table_schema = current_schema() AND table_name = 'exchange_requests'
  ) THEN
    ALTER TABLE exchange_requests ADD COLUMN IF NOT EXISTS scheduled_start_at TIMESTAMPTZ;
    ALTER TABLE exchange_requests ADD COLUMN IF NOT EXISTS reminder_sent BOOLEAN NOT NULL DEFAULT false;
  END IF;
END
$migration$;
