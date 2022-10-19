package com.xxl.core.service.impl;

import com.xxl.core.entity.UserLoginRecord;
import com.xxl.core.mapper.UserLoginRecordMapper;
import com.xxl.core.service.UserLoginRecordService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 用户登录记录表 服务实现类
 * </p>
 *
 * @author xxl
 * @since 2022-10-01
 */
@Service
public class UserLoginRecordServiceImpl extends ServiceImpl<UserLoginRecordMapper, UserLoginRecord> implements UserLoginRecordService {

    @Override
    public void recordLogin(String userIp, Long userId) {
        UserLoginRecord loginRecord = new UserLoginRecord();
        loginRecord.setIp(userIp);
        loginRecord.setUserId(userId);
        baseMapper.insert(loginRecord);
    }

    @Override
    public List<UserLoginRecord> getloginList(Integer userId) {
        List<UserLoginRecord> loginRecordList = baseMapper.list(userId);
        return loginRecordList;
    }
}
