package cn.codepractice.oj.utils;

import org.apache.commons.lang3.StringUtils;

/**
 * SQL 
 *
 */
public class SqlUtils {

    /**
     * （ SQL ）
     *
     * @param sortField
     * @return
     */
    public static boolean validSortField(String sortField) {
        if (StringUtils.isBlank(sortField)) {
            return false;
        }
        return !StringUtils.containsAny(sortField, "=", "(", ")", " ");
    }
}
