package com.xxl.core.service;

import com.xxl.core.entity.UserBind;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xxl.core.vo.UserBindVO;

import java.util.Map;

/**
 * <p>
 * 用户绑定表 服务类
 * </p>
 *
 * @author xxl
 * @since 2022-10-01
 */
public interface UserBindService extends IService<UserBind> {

    String commit(Long userId, UserBindVO userBindVO);


    void notify(Map<String, Object> switchMap);

    String getBindCodeByUserId(Long investUserId);
}
