package com.xxl.core.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xxl.core.entity.Borrower;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xxl.core.vo.BorrowerApprovalVO;
import com.xxl.core.vo.BorrowerDetailVO;
import com.xxl.core.vo.BorrowerVO;

/**
 * <p>
 * 借款人 服务类
 * </p>
 *
 * @author xxl
 * @since 2022-10-01
 */
public interface BorrowerService extends IService<Borrower> {

    void saveBorrower(BorrowerVO borrowerVO, Long userId);

    Integer getStatusByUserId(Long userId);

    IPage<Borrower> auditList(Page<Borrower> borrowerPage, String keyword);


    BorrowerDetailVO getBorrowerInfo(Long id);

    void approval(BorrowerApprovalVO borrowerApprovalVO);


    Borrower getByUserId(Long userId);
}
