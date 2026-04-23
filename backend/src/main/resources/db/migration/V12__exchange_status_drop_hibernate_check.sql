-- Hibernate, exchange_requests.status için (PENDING,ACCEPTED,REJECTED,COMPLETED) ile CHECK
-- eklemiş olabilir. Java tarafında CANCELLED eklendikten sonra INSERT/UPDATE ihlal eder.
-- Kısıt adı sürüme göre değişebildiği için tanımı 'status' içeren CHECK'leri bırakıyoruz.
DO $mig$
DECLARE
  con record;
BEGIN
  FOR con IN
    SELECT c.conname
    FROM pg_constraint c
    JOIN pg_class t ON c.conrelid = t.oid
    JOIN pg_namespace n ON t.relnamespace = n.oid
    WHERE n.nspname = current_schema()
      AND t.relname = 'exchange_requests'
      AND c.contype = 'c'
      AND pg_get_constraintdef(c.oid) ILIKE '%status%'
  LOOP
    EXECUTE format('ALTER TABLE exchange_requests DROP CONSTRAINT %I', con.conname);
  END LOOP;
END
$mig$;
