package com.xxl.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xxl.common.utils.constant.AuthServerConstant;
import com.xxl.common.utils.constant.UserStatus;
import com.xxl.common.utils.Exception.Assert;
import com.xxl.common.utils.Exception.BusinessException;
import com.xxl.common.utils.Jwt.JwtUtils;
import com.xxl.common.utils.phoneUtils.RegexValidateUtils;
import com.xxl.common.utils.result.ResponseEnum;
import com.xxl.core.entity.UserInfo;
import com.xxl.core.mapper.UserInfoMapper;
import com.xxl.core.service.UserInfoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xxl.core.service.UserLoginRecordService;
import com.xxl.core.vo.LoginVO;
import com.xxl.core.vo.RegisterVO;
import com.xxl.core.vo.UserInfoQuery;
import com.xxl.core.vo.UserInfoVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * <p>
 * 用户基本信息 服务实现类
 * </p>
 *
 * @author xxl
 * @since 2022-10-01
 */
@Service
public class UserInfoServiceImpl extends ServiceImpl<UserInfoMapper, UserInfo> implements UserInfoService {
    @Autowired
    RedisTemplate redisTemplate;

    @Autowired
    UserLoginRecordService userLoginRecordService;
    @Override
    public void register(RegisterVO registerVO) {
        //获取注册信息，进行校验
        String code = registerVO.getCode();
        String mobile = registerVO.getMobile();
        String password = registerVO.getPassword();
        Integer userType = registerVO.getUserType();
        //MOBILE_NULL_ERROR(-202, "手机号不能为空"),
        Assert.notEmpty(mobile, ResponseEnum.MOBILE_NULL_ERROR);
        //MOBILE_ERROR(-203, "手机号不正确"),
        Assert.isTrue(RegexValidateUtils.checkCellphone(mobile), ResponseEnum.MOBILE_ERROR);
        //PASSWORD_NULL_ERROR(-204, "密码不能为空"),
        Assert.notEmpty(password, ResponseEnum.PASSWORD_NULL_ERROR);

        String rediscode  = (String) redisTemplate.opsForValue().get(AuthServerConstant.SMS_CODE_CACHE_PREFIX + mobile);
        if (!StringUtils.isEmpty(rediscode)){
          if (code.equals(rediscode.split("_")[0])){
              //删除验证码;令牌机制
              redisTemplate.delete(AuthServerConstant.SMS_CODE_CACHE_PREFIX + mobile);
              this.checkPhoneUnique(registerVO.getMobile());
              UserInfo userInfo = new UserInfo();
              userInfo.setMobile(registerVO.getMobile());
              userInfo.setUserType(registerVO.getUserType());
              //密码加密
              BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
              String encodePassword = bCryptPasswordEncoder.encode(registerVO.getPassword());
              userInfo.setPassword(encodePassword);
              baseMapper.insert(userInfo);
          }else {
              throw new BusinessException(ResponseEnum.CODE_ERROR);
          }
        }else {
            throw new BusinessException(ResponseEnum.CODE_NULL_ERROR);
        }
    }

    @Override
    public UserInfoVO login(LoginVO loginVO, String userIp) {
        UserInfo userInfo = baseMapper.selectOne(new QueryWrapper<UserInfo>().eq("mobile", loginVO.getMobile()).eq("user_type",loginVO.getUserType()));
        if (StringUtils.isEmpty(userInfo)&& !loginVO.getUserType().equals(userInfo.getUserType())){
            throw new BusinessException(ResponseEnum.LOGIN_MOBILE_ERROR);
        }
        if (userInfo.getStatus()== UserStatus.STATUS_LOCKED){
            throw new BusinessException(ResponseEnum.LOGIN_LOKED_ERROR);
        }
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        //对比密码
        if (!bCryptPasswordEncoder.matches(loginVO.getPassword(),userInfo.getPassword())){
            throw new BusinessException(ResponseEnum.LOGIN_PASSWORD_ERROR);
        }else {
            //记录登录日志
            userLoginRecordService.recordLogin(userIp,userInfo.getId());

            //生成token
            String token = JwtUtils.createToken(userInfo.getId(), userInfo.getName());
            UserInfoVO userInfoVO = new UserInfoVO();
            userInfoVO.setToken(token);
            userInfoVO.setName(userInfo.getName());
            userInfoVO.setNickName(userInfo.getNickName());
            userInfoVO.setHeadImg(userInfo.getHeadImg());
            userInfoVO.setMobile(userInfo.getMobile());
            userInfoVO.setUserType(userInfoVO.getUserType());
            return userInfoVO;
        }
    }

    @Override
    public IPage<UserInfo> getPage(Page<UserInfo> infoPage, UserInfoQuery userInfoQuery) {
        String mobile = userInfoQuery.getMobile();
        Integer status = userInfoQuery.getStatus();
        Integer userType = userInfoQuery.getUserType();
        QueryWrapper<UserInfo> userInfoQueryWrapper = new QueryWrapper<>();
        if(userInfoQuery == null){
            return baseMapper.selectPage(infoPage, null);
        }else {
            userInfoQueryWrapper
                    .eq(!StringUtils.isEmpty(mobile), "mobile", mobile)
                    .eq(status != null, "status", userInfoQuery.getStatus())
                    .eq(userType != null, "user_type", userType);
            return baseMapper.selectPage(infoPage, userInfoQueryWrapper);
        }

    }

    @Override
    public void lock(Integer id, Integer status) {
        UserInfo userInfo = baseMapper.selectById(id);
        userInfo.setStatus(status);
        baseMapper.updateById(userInfo);

    }

    @Override
    public UserInfo getUserByBindCode(String bindCode) {
      return   baseMapper.selectOne(new QueryWrapper<UserInfo>().eq("bind_code", bindCode));


    }


    private void checkPhoneUnique(String mobile) {
        Integer count = baseMapper.selectCount(new QueryWrapper<UserInfo>().eq("mobile", mobile));
        if (count>0){
            throw new BusinessException(ResponseEnum.MOBILE_EXIST_ERROR);
        }
    }
}
