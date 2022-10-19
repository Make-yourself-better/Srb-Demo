package com.xxl.core.feign;


import com.xxl.common.utils.result.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient("ThirdService")
public interface SmsFeignService {
    @GetMapping("api/ali/sms/sendCode")
    R sendCode(@RequestParam("mobile") String mobile, @RequestParam("code") String code);
}
