package com.xxl.core.service;

import com.xxl.core.entity.BorrowerAttach;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 借款人上传资源表 服务类
 * </p>
 *
 * @author xxl
 * @since 2022-10-01
 */
public interface BorrowerAttachService extends IService<BorrowerAttach> {

    void saveBorrowerAttach(List<BorrowerAttach> borrowerAttachList, Long id);

    List<BorrowerAttach> getAttachInfolist(Long id);
}
