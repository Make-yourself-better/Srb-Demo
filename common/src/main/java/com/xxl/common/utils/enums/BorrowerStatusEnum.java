package com.xxl.common.utils.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@AllArgsConstructor
@Getter
//@ToString
public enum BorrowerStatusEnum {

    NO_AUTH(0, "未认证"),
    AUTH_RUN(1, "认证中"),
    AUTH_OK(2, "认证成功"),
    AUTH_FAIL(-1, "认证失败"),
    ;

    private Integer status;
    private String msg;

    public static String getMsgByStatuss(int status) {
        BorrowerStatusEnum borrowerStatusEnums[] = BorrowerStatusEnum.values();
        for (BorrowerStatusEnum borrowerStatusEnum : borrowerStatusEnums) {
            if (status == borrowerStatusEnum.getStatus().intValue()) {
                return borrowerStatusEnum.getMsg();
            }
        }
        return "";
    }

    public static String getMsgByStatus(Integer status) {
        for (BorrowerStatusEnum value : BorrowerStatusEnum.values()) {
            if (value.getStatus().intValue()==status){
                return value.getMsg();
            }
        }
        return null;
    }
}

