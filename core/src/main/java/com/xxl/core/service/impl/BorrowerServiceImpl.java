package com.xxl.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xxl.common.utils.Exception.BusinessException;
import com.xxl.common.utils.enums.BorrowerStatusEnum;
import com.xxl.common.utils.enums.IntegralEnum;
import com.xxl.common.utils.result.ResponseEnum;
import com.xxl.core.entity.Borrower;
import com.xxl.core.entity.BorrowerAttach;
import com.xxl.core.entity.UserInfo;
import com.xxl.core.entity.UserIntegral;
import com.xxl.core.mapper.BorrowerMapper;
import com.xxl.core.service.*;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xxl.core.vo.BorrowerApprovalVO;
import com.xxl.core.vo.BorrowerAttachVO;
import com.xxl.core.vo.BorrowerDetailVO;
import com.xxl.core.vo.BorrowerVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 借款人 服务实现类
 * </p>
 *
 * @author xxl
 * @since 2022-10-01
 */
@Service
public class BorrowerServiceImpl extends ServiceImpl<BorrowerMapper, Borrower> implements BorrowerService {
    @Autowired
    UserInfoService userInfoService;
    @Autowired
    BorrowerAttachService borrowerAttachService;
    @Autowired
    DictService dictService;
    @Autowired
    UserIntegralService userIntegralService;

    @Override
    public void saveBorrower(BorrowerVO borrowerVO, Long userId) {
        Borrower borrowerInfo = baseMapper.selectOne(new QueryWrapper<Borrower>().eq("user_id", userId));
        if (StringUtils.isEmpty(borrowerInfo)){
            UserInfo userInfo = userInfoService.getById(userId);
            Borrower borrower = new Borrower();
            borrower.setName(userInfo.getName());
            borrower.setUserId(userId);
            borrower.setIdCard(userInfo.getIdCard());
            borrower.setMobile(userInfo.getMobile());
            borrower.setStatus(BorrowerStatusEnum.AUTH_RUN.getStatus());//认证中
            BeanUtils.copyProperties(borrowerVO,borrower);
            baseMapper.insert(borrower);
            //保存图片附件
            borrowerAttachService.saveBorrowerAttach(borrowerVO.getBorrowerAttachList(),borrower.getId());
            //更新会员状态，更新为认证中
            userInfo.setBorrowAuthStatus(BorrowerStatusEnum.AUTH_RUN.getStatus());
            userInfoService.updateById(userInfo);
        }else {
            throw new BusinessException(ResponseEnum.USER_IS_REVIEWING);
        }
    }

    @Override
    public Integer getStatusByUserId(Long userId) {
        Borrower borrower = baseMapper.selectOne(new QueryWrapper<Borrower>().select("status").eq("user_id", userId));
        if (StringUtils.isEmpty(borrower)){
            //借款人尚未提交信息
            return BorrowerStatusEnum.NO_AUTH.getStatus();
        }
        return borrower.getStatus();
    }

    @Override
    public IPage<Borrower> auditList(Page<Borrower> borrowerPage, String keyword) {
        if (StringUtils.isEmpty(keyword)){
            return baseMapper.selectPage(borrowerPage, null);
        }else {
            QueryWrapper<Borrower> borrowerQueryWrapper = new QueryWrapper<Borrower>()
                    .like("name", keyword).or()
                    .like("id_card", keyword)
                    .or().like("mobile", keyword).orderByDesc("id");
            return baseMapper.selectPage(borrowerPage,borrowerQueryWrapper);
        }

    }

    @Override
    public BorrowerDetailVO getBorrowerInfo(Long id) {
        //查询Borrower信息
        Borrower borrower = baseMapper.selectById(id);
        BorrowerDetailVO borrowerDetailVO = new BorrowerDetailVO();
        BeanUtils.copyProperties(borrower,borrowerDetailVO);
        //婚否
        borrowerDetailVO.setMarry(borrower.getMarry()?"是":"否");
        //性别
        borrowerDetailVO.setSex(borrower.getSex()==1?"男":"女");
        //计算下拉列表选中内容
        String education = dictService.getNameByParentDictCodeAndValue("education", borrower.getEducation());
        String industry = dictService.getNameByParentDictCodeAndValue("moneyUse", borrower.getIndustry());
        String income = dictService.getNameByParentDictCodeAndValue("income", borrower.getIncome());
        String returnSource = dictService.getNameByParentDictCodeAndValue("returnSource", borrower.getReturnSource());
        String contactsRelation = dictService.getNameByParentDictCodeAndValue("relation", borrower.getContactsRelation());
        //设置下拉列表选中内容
        borrowerDetailVO.setEducation(education);
        borrowerDetailVO.setIndustry(industry);
        borrowerDetailVO.setIncome(income);
        borrowerDetailVO.setReturnSource(returnSource);
        borrowerDetailVO.setContactsRelation(contactsRelation);

        //审批状态
        String status = BorrowerStatusEnum.getMsgByStatus(borrower.getStatus());
        borrowerDetailVO.setStatus(status);



        //组装图片信息
        List<BorrowerAttach> borrowerAttachList= borrowerAttachService.getAttachInfolist(id);
        List<BorrowerAttachVO> collect = borrowerAttachList.stream().map(attach -> {
            BorrowerAttachVO borrowerAttachVO = new BorrowerAttachVO();
            BeanUtils.copyProperties(attach, borrowerAttachVO);
            return borrowerAttachVO;
        }).collect(Collectors.toList());
        borrowerDetailVO.setBorrowerAttachVOList(collect);
        return borrowerDetailVO;
    }

    @Override
    public void approval(BorrowerApprovalVO borrowerApprovalVO) {

        Borrower borrower = baseMapper.selectById(borrowerApprovalVO.getBorrowerId());
        borrower.setStatus(borrowerApprovalVO.getStatus());
        baseMapper.updateById(borrower);
        //获取用户信息
        UserInfo userInfo = userInfoService.getById(borrower.getUserId());

        //添加积分
        UserIntegral userIntegral = new UserIntegral();
        userIntegral.setUserId(borrower.getUserId());
        userIntegral.setIntegral(borrowerApprovalVO.getInfoIntegral());
        userIntegral.setContent("借款人基本信息");
        userIntegralService.save(userIntegral);
        if(borrowerApprovalVO.getInfoIntegral()!=null){
            int curIntegral = userInfo.getIntegral() + borrowerApprovalVO.getInfoIntegral();
            if(borrowerApprovalVO.getIsIdCardOk()) {
                curIntegral += IntegralEnum.BORROWER_IDCARD.getIntegral();
                userIntegral = new UserIntegral();
                userIntegral.setUserId(borrower.getUserId());
                userIntegral.setIntegral(IntegralEnum.BORROWER_IDCARD.getIntegral());
                userIntegral.setContent(IntegralEnum.BORROWER_IDCARD.getMsg());
                userIntegralService.save(userIntegral);
            }
            if(borrowerApprovalVO.getIsHouseOk()) {
                curIntegral += IntegralEnum.BORROWER_HOUSE.getIntegral();
                userIntegral = new UserIntegral();
                userIntegral.setUserId(borrower.getUserId());
                userIntegral.setIntegral(IntegralEnum.BORROWER_HOUSE.getIntegral());
                userIntegral.setContent(IntegralEnum.BORROWER_HOUSE.getMsg());
                userIntegralService.save(userIntegral);
            }

            if(borrowerApprovalVO.getIsCarOk()) {
                curIntegral += IntegralEnum.BORROWER_CAR.getIntegral();
                userIntegral = new UserIntegral();
                userIntegral.setUserId(borrower.getUserId());
                userIntegral.setIntegral(IntegralEnum.BORROWER_CAR.getIntegral());
                userIntegral.setContent(IntegralEnum.BORROWER_CAR.getMsg());
                userIntegralService.save(userIntegral);
            }
            userInfo.setIntegral(curIntegral);
            //修改审核状态
            userInfo.setBorrowAuthStatus(borrowerApprovalVO.getStatus());
            userInfoService.updateById(userInfo);
        }else {
            throw new BusinessException(ResponseEnum.INTEGRAL_ISNOTNULL);
        }
    }

    @Override
    public Borrower getByUserId(Long userId) {
        Borrower borrower = baseMapper.selectOne(new QueryWrapper<Borrower>().eq("user_id", userId));
        return borrower;

    }


}
