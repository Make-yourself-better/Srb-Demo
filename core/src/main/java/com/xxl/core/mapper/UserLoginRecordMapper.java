package com.xxl.core.mapper;

import com.xxl.core.entity.UserLoginRecord;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 用户登录记录表 Mapper 接口
 * </p>
 *
 * @author xxl
 * @since 2022-10-01
 */
public interface UserLoginRecordMapper extends BaseMapper<UserLoginRecord> {


    List<UserLoginRecord> list(@Param("userId") Integer userId);
}
