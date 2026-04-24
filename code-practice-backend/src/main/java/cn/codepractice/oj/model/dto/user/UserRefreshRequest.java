package cn.codepractice.oj.model.dto.user;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserRefreshRequest implements Serializable {

    private String refreshToken;

    private static final long serialVersionUID = 1L;
}
