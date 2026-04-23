-- Hibernate önce varchar(255) oluşturmuş olabilir; Flyway V3 ADD IF NOT EXISTS mevcut sütunu değiştirmez.
-- Uzun base64 kesilmeden saklanabilsin diye TEXT zorunlu.
DO $migration$
BEGIN
  IF EXISTS (
    SELECT 1 FROM information_schema.columns
    WHERE table_schema = current_schema()
      AND table_name = 'users'
      AND column_name = 'avatar_url'
  ) THEN
    ALTER TABLE users
      ALTER COLUMN avatar_url TYPE TEXT USING (avatar_url::text);
  END IF;
END
$migration$;
