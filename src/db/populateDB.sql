-- Заполняем таблицу услуг
INSERT INTO services (name, duration_min, is_active)
VALUES
    ('Классический массаж', 60, TRUE),
    ('Глубокотканный массаж', 90, TRUE),
    ('Расслабляющий массаж', 45, TRUE),
    ('Стоун-терапия', 75, TRUE),
    ('Тайский массаж', 120, TRUE)
    ON CONFLICT (name) DO NOTHING;

-- Пример записи для теста (UTC)
INSERT INTO appointments (service_id, start_at, end_at, customer_name, customer_phone, comment)
VALUES (
           (SELECT id FROM services WHERE name = 'Классический массаж'),
           '2025-10-27 10:00:00+00',
           '2025-10-27 10:00:00+00', -- триггер заменит
           'Artem Orlov',
           '+49 000 000 00',
           'Хочу полегче в области шеи'
       );

SELECT 'Data populated successfully!' AS status;
