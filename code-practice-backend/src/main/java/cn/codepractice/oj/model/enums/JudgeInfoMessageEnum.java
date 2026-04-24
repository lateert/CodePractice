package cn.codepractice.oj.model.enums;

import org.apache.commons.lang3.ObjectUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/** Normalized judge outcome / error label (stored in judge JSON). */
public enum JudgeInfoMessageEnum {

    ACCEPTED("Успешно", "Accepted"),
    WRONG_ANSWER("Неверный ответ", "wrong Answer"),
    COMPILE_ERROR("Ошибка компиляции", "Compile Error"),
    MEMORY_LIMIT_EXCEEDED("Превышен лимит памяти", "memory limit exceeded"),
    TIME_LIMIT_EXCEEDED("Превышено время", "Time Limit Exceeded"),
    PRESENTATION_ERROR("Ошибка формата вывода", "Presentation Error"),
    WAITING("Ожидание", "Waiting"),
    OUTPUT_LIMIT_EXCEEDED("Превышен лимит вывода", "Output Limit Exceeded"),
    DANGEROUS_OPERATION("Запрещённая операция", "Dangerous Operation"),
    RUNTIME_ERROR("Ошибка выполнения", "Runtime Error"),
    SYSTEM_ERROR("Системная ошибка", "System Error");

    private final String text;

    private final String value;

    JudgeInfoMessageEnum(String text, String value) {
        this.text = text;
        this.value = value;
    }

    /** All machine-readable {@code value} strings (e.g. {@code Accepted}). */
    public static List<String> getValues() {
        return Arrays.stream(values()).map(item -> item.value).collect(Collectors.toList());
    }

    /** Resolve by machine {@code value} string. */
    public static JudgeInfoMessageEnum getEnumByValue(String value) {
        if (ObjectUtils.isEmpty(value)) {
            return null;
        }
        for (JudgeInfoMessageEnum anEnum : JudgeInfoMessageEnum.values()) {
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
