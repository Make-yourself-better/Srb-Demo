package com.xxl.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xxl.core.entity.BorrowerAttach;
import com.xxl.core.mapper.BorrowerAttachMapper;
import com.xxl.core.service.BorrowerAttachService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 借款人上传资源表 服务实现类
 * </p>
 *
 * @author xxl
 * @since 2022-10-01
 */
@Service
public class BorrowerAttachServiceImpl extends ServiceImpl<BorrowerAttachMapper, BorrowerAttach> implements BorrowerAttachService {

    @Override
    public void saveBorrowerAttach(List<BorrowerAttach> borrowerAttachList, Long id) {
        borrowerAttachList.stream().forEach(attach -> {
            BorrowerAttach borrowerAttach = new BorrowerAttach();
            borrowerAttach.setImageName(attach.getImageName());
            borrowerAttach.setImageType(attach.getImageType());
            borrowerAttach.setImageUrl(attach.getImageUrl());
            borrowerAttach.setBorrowerId(id);
            baseMapper.insert(borrowerAttach);
        });

    }

    @Override
    public List<BorrowerAttach> getAttachInfolist(Long id) {
        List<BorrowerAttach> borrowerAttachList = baseMapper.selectList(new QueryWrapper<BorrowerAttach>().eq("borrower_id", id));
        return borrowerAttachList;

    }
}
