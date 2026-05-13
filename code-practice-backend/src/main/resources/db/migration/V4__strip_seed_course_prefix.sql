-- Убираем устаревший префикс сид-данных из названия и описания курса.
UPDATE course
SET description = TRIM(REPLACE(description, 'Сид-курс:', ''))
WHERE description IS NOT NULL
  AND description LIKE 'Сид-курс:%';

UPDATE course
SET title = TRIM(REPLACE(title, 'Сид-курс:', ''))
WHERE title IS NOT NULL
  AND title LIKE 'Сид-курс:%';
