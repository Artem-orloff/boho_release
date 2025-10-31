-- =============================
--  Настройка окружения
-- =============================
\connect booking_alpha booking_user

-- включаем расширение (на будущее)
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- =============================
--  Таблица: услуги
-- =============================
DROP TABLE IF EXISTS appointments CASCADE;
DROP TABLE IF EXISTS services CASCADE;

CREATE TABLE IF NOT EXISTS services (
                                        id            BIGSERIAL PRIMARY KEY,
                                        name          TEXT        NOT NULL UNIQUE,
                                        duration_min  INTEGER     NOT NULL CHECK (duration_min BETWEEN 5 AND 480),
    is_active     BOOLEAN     NOT NULL DEFAULT TRUE,
    created_at    TIMESTAMPTZ NOT NULL DEFAULT NOW()
    );

CREATE INDEX IF NOT EXISTS idx_services_active ON services(is_active);

-- =============================
--  Таблица: записи
-- =============================
CREATE TABLE IF NOT EXISTS appointments (
                                            id             BIGSERIAL PRIMARY KEY,
                                            service_id     BIGINT      NOT NULL REFERENCES services(id) ON UPDATE CASCADE ON DELETE RESTRICT,
    start_at       TIMESTAMPTZ NOT NULL,
    end_at         TIMESTAMPTZ NOT NULL,
    customer_name  TEXT,
    customer_phone TEXT,
    comment        TEXT,
    created_at     TIMESTAMPTZ NOT NULL DEFAULT NOW()
    );

CREATE INDEX IF NOT EXISTS idx_appt_service_start ON appointments(service_id, start_at);
CREATE INDEX IF NOT EXISTS idx_appt_start ON appointments(start_at);

-- =============================
--  Триггер: автозаполнение end_at
-- =============================
CREATE OR REPLACE FUNCTION trg_set_end_at()
RETURNS TRIGGER AS $$
BEGIN
SELECT NEW.start_at + make_interval(mins => s.duration_min)
INTO NEW.end_at
FROM services s
WHERE s.id = NEW.service_id;

IF NEW.end_at IS NULL THEN
        RAISE EXCEPTION 'Unknown service_id=%', NEW.service_id;
END IF;

RETURN NEW;
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS set_end_at_before_ins_upd ON appointments;
CREATE TRIGGER set_end_at_before_ins_upd
    BEFORE INSERT OR UPDATE OF service_id, start_at ON appointments
    FOR EACH ROW
    EXECUTE FUNCTION trg_set_end_at();

-- =============================
-- Проверка
-- =============================
SELECT 'Schema created successfully!' AS status;
