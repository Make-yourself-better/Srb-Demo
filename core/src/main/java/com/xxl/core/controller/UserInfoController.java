package com.xxl.core.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xxl.common.utils.constant.AuthServerConstant;
import com.xxl.common.utils.Exception.BusinessException;
import com.xxl.common.utils.Jwt.JwtUtils;
import com.xxl.common.utils.phoneUtils.RegexValidateUtils;
import com.xxl.common.utils.result.R;
import com.xxl.common.utils.result.ResponseEnum;
import com.xxl.core.entity.UserInfo;
import com.xxl.core.feign.SmsFeignService;
import com.xxl.core.service.UserInfoService;
import com.xxl.core.vo.LoginVO;
import com.xxl.core.vo.RegisterVO;
import com.xxl.core.vo.UserInfoQuery;
import com.xxl.core.vo.UserInfoVO;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 用户基本信息 前端控制器
 * </p>
 *
 * @author xxl
 * @since 2022-10-01
 */
@Slf4j
@RestController
@RequestMapping("admin/core/userinfo")
public class UserInfoController {
    @Autowired
    RedisTemplate redisTemplate;
    @Autowired
    SmsFeignService smsFeignService;
    @Autowired
    UserInfoService userInfoService;


    @GetMapping("sendCode/{mobile}")
    public R sendCode(@PathVariable("mobile") String mobile){

        if(StringUtils.isEmpty(mobile)){
            throw  new BusinessException(ResponseEnum.MOBILE_NULL_ERROR);
        }
        if (!RegexValidateUtils.checkCellphone(mobile)){
            throw  new BusinessException( ResponseEnum.MOBILE_ERROR);
        }
        String redisCode = (String) redisTemplate.opsForValue().get(AuthServerConstant.SMS_CODE_CACHE_PREFIX + mobile);
        if (!StringUtils.isEmpty(redisCode)){
            long time = Long.parseLong(redisCode.split("_")[1]);//取出第二个值:时间
            if (System.currentTimeMillis()-time<60000){
                throw new BusinessException(ResponseEnum.CODE_ERROR);
            }
        }
        String code = UUID.randomUUID().toString().substring(0,5);
        String redisValue = code+ "_"+ System.currentTimeMillis();
        smsFeignService.sendCode(mobile,code);
        //存储验证码
        redisTemplate.opsForValue().set(AuthServerConstant.SMS_CODE_CACHE_PREFIX + mobile,redisValue,10, TimeUnit.MINUTES);
        log.info("code=====>{}",code);
        return  R.ok();
    }

    @PostMapping("/register")
    public R register(@RequestBody RegisterVO registerVO){
        userInfoService.register(registerVO);
        return R.ok().message("注册成功");
    }

    @PostMapping("/login")
    public R register(@RequestBody LoginVO loginVO , HttpServletRequest request){
        String UserIp = request.getRemoteAddr();
        UserInfoVO vo = userInfoService.login(loginVO,UserIp);
        return R.ok().data("userInfo", vo);
    }
    @ApiOperation("校验令牌")
    @GetMapping("/checkToken")
    public R checkToken(HttpServletRequest request){
        String userInfo = request.getHeader("token");
       if ( JwtUtils.checkToken(userInfo)){
           return R.ok();
       }else {
           return R.setResult(ResponseEnum.LOGIN_AUTH_ERROR);
       }
    }
    //关键字查询
    @GetMapping("getUserInfo/{page}/{limit}")
    public R getKeyword(@PathVariable("page") Long page,@PathVariable("limit") Long limit,
                        @ApiParam(value = "查询对象", required = false) //不是必须填
                      UserInfoQuery userInfoQuery){
        Page<UserInfo> infoPage = new Page<>(page, limit);
        IPage<UserInfo> pageModel = userInfoService.getPage(infoPage,userInfoQuery);
        return R.ok().data("page",pageModel);
    }
    @PutMapping("lock/{id}/{status}")
    public R lock(@PathVariable("id")Integer id,@PathVariable("status") Integer status){
        userInfoService.lock(id,status);
        return R.ok().message(status==1?"解锁成功":"锁定成功");

    }
}

