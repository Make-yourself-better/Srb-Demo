package com.xxl.core.controller.web;


import com.alibaba.fastjson.JSON;
import com.xxl.common.utils.Jwt.JwtUtils;
import com.xxl.common.utils.result.R;
import com.xxl.core.entity.UserBind;
import com.xxl.core.hfb.RequestHelper;
import com.xxl.core.service.UserBindService;
import com.xxl.core.vo.UserBindVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 用户绑定表 前端控制器
 * </p>
 *
 * @author xxl
 * @since 2022-10-01
 */
@Slf4j
@RestController
@RequestMapping("/admin/core/user-bind")
public class UserBindController {
    @Autowired
    UserBindService userBindService;

    @ApiOperation("账户绑定提交数据")
    @PostMapping("/auth/bind")
    public R userBind(@RequestBody UserBindVO userBindVO, HttpServletRequest request){
        String token = request.getHeader("token");
        Long userId = JwtUtils.getUserId(token);
        String commit = userBindService.commit(userId, userBindVO);
        return R.ok().data("formStr", commit);
    }
    @ApiOperation("账户绑定异步回调")
    @PostMapping("/notify")
    public String notify(HttpServletRequest request){
        Map<String, Object> switchMap = RequestHelper.switchMap(request.getParameterMap());
        log.info("用户账号绑定异步回调：" + JSON.toJSONString(switchMap));
        //校验签名
        if (!RequestHelper.isSignEquals(switchMap)){
            log.error("用户账号绑定异步回调签名错误：" + JSON.toJSONString(switchMap));
            return "fail";
        }
        userBindService.notify(switchMap);
        return "success";
    }
}

