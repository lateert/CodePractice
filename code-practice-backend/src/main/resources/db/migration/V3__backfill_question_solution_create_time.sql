-- Старые разборы могли сохраняться без create_time → на фронте moment(null) давал «Invalid date».
UPDATE question_solution
SET create_time = CURRENT_TIMESTAMP
WHERE create_time IS NULL
  AND (is_delete = 0 OR is_delete IS NULL);
