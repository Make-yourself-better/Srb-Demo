package com.xxl.core.service;

import com.xxl.core.entity.UserAccount;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xxl.core.vo.TransFlowBO;

import java.math.BigDecimal;
import java.util.Map;

/**
 * <p>
 * 用户账户 服务类
 * </p>
 *
 * @author xxl
 * @since 2022-10-01
 */
public interface UserAccountService extends IService<UserAccount> {

    String commitCharge(BigDecimal chargeAmt, Long userId);

    String notify(Map<String, Object> paramMap);

    BigDecimal getAccount(Long userId);
}
