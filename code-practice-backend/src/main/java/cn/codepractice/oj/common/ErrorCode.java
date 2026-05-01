package cn.codepractice.oj.common;

public enum ErrorCode {

    SUCCESS(0, "ok"),
    PARAMS_ERROR(40000, "Ошибка параметров запроса"),
    NOT_LOGIN_ERROR(40100, "Необходима авторизация"),
    NO_AUTH_ERROR(40101, "Нет доступа"),
    NOT_FOUND_ERROR(40400, "Данные не найдены"),
    FORBIDDEN_ERROR(40300, "Доступ запрещён"),
    SYSTEM_ERROR(50000, "Внутренняя ошибка сервера"),
    OPERATION_ERROR(50001, "Ошибка выполнения"),
    API_REQUEST_ERROR(50010, "Ошибка вызова интерфейса");

    private final int code;

    private final String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

}
