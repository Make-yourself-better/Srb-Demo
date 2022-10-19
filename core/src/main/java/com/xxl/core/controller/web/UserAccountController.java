package com.xxl.core.controller.web;


import com.alibaba.fastjson.JSON;
import com.xxl.common.utils.Jwt.JwtUtils;
import com.xxl.common.utils.result.R;
import com.xxl.core.entity.UserAccount;
import com.xxl.core.hfb.RequestHelper;
import com.xxl.core.service.UserAccountService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.Map;

/**
 * <p>
 * 用户账户 前端控制器
 * </p>
 *
 * @author xxl
 * @since 2022-10-01
 */
@Slf4j
@RestController
@RequestMapping("/admin/core/userAccount")
public class UserAccountController {
    @Autowired
    UserAccountService userAccountService;
    @ApiOperation("充值")
    @PostMapping("/auth/commitCharge/{chargeAmt}")
    public R commitCharge(@PathVariable BigDecimal chargeAmt, HttpServletRequest request){
        Long userId = JwtUtils.getUserId(request.getHeader("token"));
        String formStr = userAccountService.commitCharge(chargeAmt, userId);
        return R.ok().data("formStr", formStr);
    }
    @PostMapping("/notify")
    public String notify(HttpServletRequest request){
        Map<String, Object> paramMap = RequestHelper.switchMap(request.getParameterMap());
        log.info("用户充值异步回调：" + JSON.toJSONString(paramMap));
        if (RequestHelper.isSignEquals(paramMap)){
            //充值成功交易返回的结果码 0001
            if (paramMap.get("resultCode").equals("0001")){
                return userAccountService.notify(paramMap);
            }else {
                log.info("用户充值异步回调充值失败：" + JSON.toJSONString(paramMap));
                return "sucess";
            }
        }else {
            log.info("用户充值异步回调签名错误：" + JSON.toJSONString(paramMap));
            return "fail";
        }
    }
    @ApiOperation("查询账户余额")
    @GetMapping("/auth/getAccount")
    public R getAccount(HttpServletRequest request){
        Long userId = JwtUtils.getUserId(request.getHeader("token"));
        BigDecimal account = userAccountService.getAccount(userId);
        return R.ok().data("account", account);
    }

}

