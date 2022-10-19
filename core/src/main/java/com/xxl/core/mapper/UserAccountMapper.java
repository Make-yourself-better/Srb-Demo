package com.xxl.core.mapper;

import com.xxl.core.entity.UserAccount;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.math.BigDecimal;

/**
 * <p>
 * 用户账户 Mapper 接口
 * </p>
 *
 * @author xxl
 * @since 2022-10-01
 */
public interface UserAccountMapper extends BaseMapper<UserAccount> {

    void updateAccount(String bindCode, BigDecimal bigDecimal, BigDecimal bigDecimal1);
}
