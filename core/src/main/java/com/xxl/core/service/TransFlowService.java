package com.xxl.core.service;

import com.xxl.core.entity.TransFlow;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xxl.core.vo.TransFlowBO;

/**
 * <p>
 * 交易流水表 服务类
 * </p>
 *
 * @author xxl
 * @since 2022-10-01
 */
public interface TransFlowService extends IService<TransFlow> {
    void saveTransFlow(TransFlowBO transFlowBO);


}
