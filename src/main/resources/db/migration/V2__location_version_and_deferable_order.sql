-- Проблема 3: версія для оптимістичного блокування локацій
ALTER TABLE locations
    ADD COLUMN version integer NOT NULL DEFAULT 1 CHECK (version > 0);

-- Проблема 2: у схемі вже є unique_plan_order, але він НЕ відкладений.
-- При зсуві порядку (visit_order = visit_order + 1) рядки тимчасово
-- збігаються, і звичайний UNIQUE може впасти посеред UPDATE.
-- Перестворюємо його як DEFERRABLE: перевірка робиться при COMMIT.
ALTER TABLE locations DROP CONSTRAINT unique_plan_order;
ALTER TABLE locations
    ADD CONSTRAINT unique_plan_order
        UNIQUE (travel_plan_id, visit_order) DEFERRABLE INITIALLY DEFERRED;

-- Оптимізація: видаляємо дублюючий індекс, оскільки UNIQUE вже створює Б-дерево для цієї пари колонок
DROP INDEX IF EXISTS idx_locations_plan_order;