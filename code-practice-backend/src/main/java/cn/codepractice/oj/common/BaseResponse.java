package cn.codepractice.oj.common;

import java.io.Serializable;
import lombok.Data;

/**
 * Standard API response wrapper (code, data, message).
 *
 * @param <T> type of {@code data}
 */
@Data
public class BaseResponse<T> implements Serializable {

    private int code;

    private T data;

    private String message;

    public BaseResponse(int code, T data, String message) {
        this.code = code;
        this.data = data;
        this.message = message;
    }

    public BaseResponse(int code, T data) {
        this(code, data, "");
    }

    public BaseResponse(ErrorCode errorCode) {
        this(errorCode.getCode(), null, errorCode.getMessage());
    }
}
