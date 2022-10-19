package com.xxl.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xxl.common.utils.enums.UserBindEnum;
import com.xxl.core.entity.UserBind;
import com.xxl.core.entity.UserInfo;
import com.xxl.core.hfb.FormHelper;
import com.xxl.core.hfb.HfbConst;
import com.xxl.core.hfb.RequestHelper;
import com.xxl.core.mapper.UserBindMapper;
import com.xxl.core.service.UserBindService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xxl.core.service.UserInfoService;
import com.xxl.core.vo.UserBindVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 用户绑定表 服务实现类
 * </p>
 *
 * @author xxl
 * @since 2022-10-01
 */
@Service
public class UserBindServiceImpl extends ServiceImpl<UserBindMapper, UserBind> implements UserBindService {
    @Autowired
    UserInfoService userInfoService;

    @Override
    public String commit(Long userId, UserBindVO userBindVO) {
        //一张身份证只能绑定一个
        UserBind bind = baseMapper.selectOne(new QueryWrapper<UserBind>().eq("user_id", userId));
        UserInfo userInfo = userInfoService.getById(userId);
        if (StringUtils.isEmpty(bind)&&userInfo.getBindStatus()==UserBindEnum.NO_BIND.getStatus()){
            //新增
            UserBind userBind = new UserBind();
            userBind.setUserId(userId);
            userBind.setMobile(userBindVO.getMobile());
            userBind.setBankType(userBindVO.getBankType());
            userBind.setIdCard(userBindVO.getIdCard());
            userBind.setBankNo(userBindVO.getBankNo());
            userBind.setName(userBindVO.getName());
            baseMapper.insert(userBind);
        }else {
            //如果用户没有绑定 就应该是新增操作
            BeanUtils.copyProperties(userBindVO,bind);
            baseMapper.updateById(bind);
        }

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("agentId", HfbConst.AGENT_ID);
        paramMap.put("agentUserId", userId);
        paramMap.put("idCard",userBindVO.getIdCard());
        paramMap.put("personalName", userBindVO.getName());
        paramMap.put("bankType", userBindVO.getBankType());
        paramMap.put("bankNo", userBindVO.getBankNo());
        paramMap.put("mobile", userBindVO.getMobile());
        paramMap.put("returnUrl", HfbConst.USERBIND_RETURN_URL);
        paramMap.put("notifyUrl", HfbConst.USERBIND_NOTIFY_URL);
        paramMap.put("timestamp", RequestHelper.getTimestamp());
        paramMap.put("sign", RequestHelper.getSign(paramMap));

        //构建充值自动提交表单
        String formStr = FormHelper.buildForm(HfbConst.USERBIND_URL, paramMap);
        return formStr;
    }
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void notify(Map<String, Object> switchMap) {
    /* 尚融宝user_bind表更新bind_code字段、status字段
    尚融宝user_info表更新 bind_code字段、name字段、idCard字段、bind_status字段*/
        String agent_user_id = (String) switchMap.get("agentUserId");
        String bind_code = (String) switchMap.get("bindCode");//会员id

        UserBind userBind = baseMapper.selectOne(new QueryWrapper<UserBind>().eq("user_id", agent_user_id));
        userBind.setBindCode(bind_code);
        userBind.setStatus(UserBindEnum.BIND_OK.getStatus());
        baseMapper.updateById(userBind);
        //获取用户信息
        UserInfo userInfo = userInfoService.getById(agent_user_id);
        userInfo.setBindStatus(UserBindEnum.BIND_OK.getStatus());
        userInfo.setBindCode(bind_code);
        userInfo.setIdCard(userBind.getIdCard());
        userInfo.setName(userBind.getName());
        userInfoService.updateById(userInfo);
    }

    @Override
    public String getBindCodeByUserId(Long investUserId) {

        QueryWrapper<UserBind> userBindQueryWrapper = new QueryWrapper<>();
        userBindQueryWrapper.eq("user_id", investUserId);
        UserBind userBind = baseMapper.selectOne(userBindQueryWrapper);
        String bindCode = userBind.getBindCode();
        return bindCode;
    }
}
