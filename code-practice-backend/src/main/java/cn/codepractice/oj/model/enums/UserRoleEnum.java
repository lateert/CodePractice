package cn.codepractice.oj.model.enums;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.lang3.ObjectUtils;

/** Роль пользователя приложения (поле {@code user.user_role}). */
public enum UserRoleEnum {

    USER("Пользователь", "user"),
    ADMIN("Администратор", "admin"),
    TEACHER("Преподаватель", "teacher"),
    BAN("Заблокирован", "ban");

    private final String text;

    private final String value;

    UserRoleEnum(String text, String value) {
        this.text = text;
        this.value = value;
    }

    /** Все строковые значения ролей {@code value}. */
    public static List<String> getValues() {
        return Arrays.stream(values()).map(item -> item.value).collect(Collectors.toList());
    }

    /** Поиск роли по сохранённому {@code value} (например, {@code teacher}). */
    public static UserRoleEnum getEnumByValue(String value) {
        if (ObjectUtils.isEmpty(value)) {
            return null;
        }
        for (UserRoleEnum anEnum : UserRoleEnum.values()) {
            if (anEnum.value.equals(value)) {
                return anEnum;
            }
        }
        return null;
    }

    public String getValue() {
        return value;
    }

    public String getText() {
        return text;
    }
}
