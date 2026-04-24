package cn.codepractice.oj.model.enums;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.lang3.ObjectUtils;

/** Application role (stored in {@code user.user_role}). */
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

    /** All role {@code value} strings. */
    public static List<String> getValues() {
        return Arrays.stream(values()).map(item -> item.value).collect(Collectors.toList());
    }

    /** Resolve role by persisted {@code value} (e.g. {@code teacher}). */
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
