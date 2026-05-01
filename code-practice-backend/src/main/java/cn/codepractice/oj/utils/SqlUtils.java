package cn.codepractice.oj.utils;

import org.apache.commons.lang3.StringUtils;

/** Утилиты для безопасной работы с параметрами сортировки в SQL. */
public class SqlUtils {

    /** Разрешённое имя поля сортировки: без операторов и скобок. */
    public static boolean validSortField(String sortField) {
        if (StringUtils.isBlank(sortField)) {
            return false;
        }
        return !StringUtils.containsAny(sortField, "=", "(", ")", " ");
    }
}
