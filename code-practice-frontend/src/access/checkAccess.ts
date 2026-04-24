import accessEnum from "@/access/accessEnum";

/**
 * Проверка прав доступа
 * @param loginUser
 * @param access
 */
const checkAccess = (loginUser: any, access: any) => {
  // resolve route access from loginUser.role
  const loginUserAccess = loginUser?.userRole ?? accessEnum.NOT_LOGIN;

  if (access === accessEnum.NOT_LOGIN) {
    return true;
  }
  if (access === accessEnum.USER) {
    if (loginUserAccess === accessEnum.NOT_LOGIN) {
      return false;
    }
  }
  if (access === accessEnum.ADMIN) {
    if (loginUserAccess !== accessEnum.ADMIN) {
      return false;
    }
  }
  if (access === accessEnum.ADMIN_OR_TEACHER) {
    if (loginUserAccess !== accessEnum.ADMIN && loginUserAccess !== accessEnum.TEACHER) {
      return false;
    }
  }
  return true;
};

export default checkAccess;
