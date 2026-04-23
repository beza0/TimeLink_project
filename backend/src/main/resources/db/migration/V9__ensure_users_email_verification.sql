-- Bazı ortamlarda V8 atlanmış veya kısmen uygulanmış olabilir; idempotent tekrar.
DO $migration$
BEGIN
  IF EXISTS (
    SELECT 1 FROM information_schema.tables
    WHERE table_schema = current_schema() AND table_name = 'users'
  ) THEN
    ALTER TABLE users ADD COLUMN IF NOT EXISTS email_verified BOOLEAN NOT NULL DEFAULT TRUE;
    ALTER TABLE users ADD COLUMN IF NOT EXISTS email_verification_token VARCHAR(128);
    ALTER TABLE users ADD COLUMN IF NOT EXISTS email_verification_expires_at TIMESTAMPTZ;

    CREATE INDEX IF NOT EXISTS idx_users_email_verification_token
        ON users (email_verification_token)
        WHERE email_verification_token IS NOT NULL;
  END IF;
END
$migration$;
