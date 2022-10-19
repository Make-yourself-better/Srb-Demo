package com.xxl.core.service;

import com.xxl.core.entity.BorrowInfo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xxl.core.vo.BorrowInfoApprovalVO;
import com.xxl.core.vo.BorrowerInfoVo;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 借款信息表 服务类
 * </p>
 *
 * @author xxl
 * @since 2022-10-01
 */
public interface BorrowInfoService extends IService<BorrowInfo> {

    BigDecimal getBorrowAmount(Long userId);

    void saveBorrowInfo(BorrowInfo borrowInfo, Long userId);

    Integer getStatusByUserId(Long userId);


    List<BorrowerInfoVo> getBorrowerList();

    void approval(BorrowInfoApprovalVO borrowInfoApprovalVO);

    Map<String, Object> getBorrowerDetail(Long id);

    BorrowInfo getByUserId(Long userId);
}
