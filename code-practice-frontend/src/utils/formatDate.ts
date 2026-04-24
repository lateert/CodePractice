import moment from "moment";

/** Дата для UI; при null/невалидном значении — пустая строка (не «Invalid date»). */
export function formatDateYMD(value: unknown): string {
  if (value == null || value === "") {
    return "";
  }
  const m = moment(value as moment.MomentInput);
  return m.isValid() ? m.format("YYYY-MM-DD") : "";
}
