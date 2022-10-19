package com.xxl.thirdservice.controller;


import com.xxl.common.utils.result.R;
import com.xxl.thirdservice.component.SmsComponent;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/ali/sms")
public class SmsController {
    @Autowired
    SmsComponent smsComponent;
    /**
     * 提供给别的服务调用
     * @param mobile
     * @param code
     * @return
     */
    @GetMapping("sendCode")
    public R sendCode(@RequestParam("mobile") String mobile, @RequestParam("code") String code){
        smsComponent.senSmsCode(mobile,code);
        return R.ok();
    }
}
