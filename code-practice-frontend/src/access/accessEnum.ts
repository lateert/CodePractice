/**
 * Определение прав доступа
 */
const accessEnum = {
  NOT_LOGIN: "notLogin",
  USER: "user",
  ADMIN: "admin",
  TEACHER: "teacher",
  /** Доступ для админа или преподавателя (курсы, задачи, отправки, статистика, отчёты) */
  ADMIN_OR_TEACHER: "adminOrTeacher",
  VIP: "vip",
};

export default accessEnum;
