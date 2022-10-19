package com.xxl.core.service;

import com.xxl.core.entity.LendItem;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xxl.core.vo.InvestVO;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 标的出借记录表 服务类
 * </p>
 *
 * @author xxl
 * @since 2022-10-01
 */
public interface LendItemService extends IService<LendItem> {

    String commitInvest(InvestVO investVO);

    void notify(Map<String, Object> paramMap);

    List<LendItem> selectByLendId(Long lendId, Integer status);
}
