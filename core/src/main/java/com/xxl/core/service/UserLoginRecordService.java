package com.xxl.core.service;

import com.xxl.core.entity.UserLoginRecord;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 用户登录记录表 服务类
 * </p>
 *
 * @author xxl
 * @since 2022-10-01
 */
public interface UserLoginRecordService extends IService<UserLoginRecord> {

    void recordLogin(String userIp, Long userId);

    List<UserLoginRecord> getloginList(Integer userId);
}
