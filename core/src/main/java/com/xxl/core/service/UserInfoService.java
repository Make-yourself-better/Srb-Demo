package com.xxl.core.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xxl.core.entity.UserInfo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xxl.core.vo.LoginVO;
import com.xxl.core.vo.RegisterVO;
import com.xxl.core.vo.UserInfoQuery;
import com.xxl.core.vo.UserInfoVO;

/**
 * <p>
 * 用户基本信息 服务类
 * </p>
 *
 * @author xxl
 * @since 2022-10-01
 */
public interface UserInfoService extends IService<UserInfo> {

    void register(RegisterVO registerVO);

    UserInfoVO login(LoginVO loginVO, String userIp);

    IPage<UserInfo> getPage(Page<UserInfo> infoPage, UserInfoQuery userInfoQuery);

    void lock(Integer id, Integer status);

    UserInfo getUserByBindCode(String bindCode);
}
