package com.xxl.core.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xxl.common.utils.Exception.BusinessException;
import com.xxl.common.utils.enums.TransTypeEnum;
import com.xxl.common.utils.phoneUtils.LendNoUtils;
import com.xxl.common.utils.result.ResponseEnum;
import com.xxl.core.entity.TransFlow;
import com.xxl.core.entity.UserAccount;
import com.xxl.core.entity.UserInfo;
import com.xxl.core.hfb.FormHelper;
import com.xxl.core.hfb.HfbConst;
import com.xxl.core.hfb.RequestHelper;
import com.xxl.core.mapper.UserAccountMapper;
import com.xxl.core.service.TransFlowService;
import com.xxl.core.service.UserAccountService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xxl.core.service.UserInfoService;
import com.xxl.core.vo.TransFlowBO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * 用户账户 服务实现类
 * </p>
 *
 * @author xxl
 * @since 2022-10-01
 */
@Slf4j
@Service
public class UserAccountServiceImpl extends ServiceImpl<UserAccountMapper, UserAccount> implements UserAccountService {
    @Autowired
    UserInfoService userInfoService;
    @Autowired
    TransFlowService transFlowService;
    @Override
    public String commitCharge(BigDecimal chargeAmt, Long userId) {
        if (StringUtils.isEmpty(userId)){
            throw new BusinessException(ResponseEnum.LOGIN_AUTH_ERROR);
        }
        UserInfo userInfo = userInfoService.getById(userId);
        if (StringUtils.isEmpty(userInfo.getBindCode())){
            throw new BusinessException(ResponseEnum.USER_NO_BIND_ERROR);
        }
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("agentId", HfbConst.AGENT_ID);
        paramMap.put("agentBillNo", LendNoUtils.getNo());
        paramMap.put("bindCode", userInfo.getBindCode());
        paramMap.put("chargeAmt", chargeAmt);
        paramMap.put("feeAmt", new BigDecimal("0"));
        paramMap.put("notifyUrl", HfbConst.RECHARGE_NOTIFY_URL);//检查常量是否正确
        paramMap.put("returnUrl", HfbConst.RECHARGE_RETURN_URL);
        paramMap.put("timestamp", RequestHelper.getTimestamp());
        String sign = RequestHelper.getSign(paramMap);
        paramMap.put("sign", sign);
        //构建充值自动提交表单
        String formStr = FormHelper.buildForm(HfbConst.RECHARGE_URL, paramMap);
        return formStr;
    }

    @Override
    public String notify(Map<String, Object> paramMap) {
        log.info("充值成功：" + JSONObject.toJSONString(paramMap));
        String bindCode = (String)paramMap.get("bindCode"); //充值人绑定协议号
        String chargeAmt = (String)paramMap.get("chargeAmt"); //充值金额
        UserInfo userInfo = userInfoService.getUserByBindCode(bindCode);
        UserAccount userAccount = new UserAccount();
        userAccount.setUserId(userInfo.getId());
        userAccount.setAmount(new BigDecimal(chargeAmt));
        userAccount.setFreezeAmount(new BigDecimal(0));
        baseMapper.insert(userAccount);

        log.info("充值成功：" + JSONObject.toJSONString(paramMap));

        //判断交易流水是否存在


        //增加交易流水
        String agentBillNo = (String)paramMap.get("agentBillNo"); //商户充值订单号
        TransFlowBO transFlowBO = new TransFlowBO(
                agentBillNo,
                bindCode,
                new BigDecimal(chargeAmt),
                TransTypeEnum.RECHARGE,
                "充值");
        transFlowService.saveTransFlow(transFlowBO);

        return "success";
    }

    @Override
    public BigDecimal getAccount(Long userId) {
        UserAccount userAccount = baseMapper.selectOne(new QueryWrapper<UserAccount>().eq("user_id", userId));
        return userAccount.getAmount();

    }


}
