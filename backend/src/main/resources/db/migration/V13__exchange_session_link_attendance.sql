-- Toplantı linki (Zoom/Meet) + öğrenci isteğe bağlı "katıldım" onayı
DO $migration$
BEGIN
  IF EXISTS (
    SELECT 1 FROM information_schema.tables
    WHERE table_schema = current_schema() AND table_name = 'exchange_requests'
  ) THEN
    ALTER TABLE exchange_requests ADD COLUMN IF NOT EXISTS session_meeting_url varchar(2000);
    ALTER TABLE exchange_requests ADD COLUMN IF NOT EXISTS requester_attendance_ack_at timestamptz;
  END IF;
END
$migration$;
