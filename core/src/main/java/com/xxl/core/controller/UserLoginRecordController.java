package com.xxl.core.controller;


import com.xxl.common.utils.result.R;
import com.xxl.core.entity.UserLoginRecord;
import com.xxl.core.service.UserLoginRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 * 用户登录记录表 前端控制器
 * </p>
 *
 * @author xxl
 * @since 2022-10-01
 */
@RestController
@RequestMapping("/admin/core/UserLoginRecord")
public class UserLoginRecordController {
    @Autowired
    UserLoginRecordService loginRecordService;
    @GetMapping("loginRecordlist/{userId}")
    public R loginRecord(@PathVariable("userId") Integer userId){
     List<UserLoginRecord> loginRecordList = loginRecordService.getloginList(userId);
     return R.ok().data("list",loginRecordList);

    }
}

