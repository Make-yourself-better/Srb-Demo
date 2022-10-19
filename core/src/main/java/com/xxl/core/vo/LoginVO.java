package com.xxl.core.vo;

import lombok.Data;

import javax.validation.constraints.NotBlank;
@Data
public class LoginVO {
    @NotBlank(message = "类型不能为空")
    private Integer userType;
    @NotBlank(message = "手机号不能为空")
    private String mobile;
    @NotBlank(message = "密码不能为空")
    private String password;

}
