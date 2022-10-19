package com.xxl.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xxl.common.utils.Exception.Assert;
import com.xxl.common.utils.Exception.BusinessException;
import com.xxl.common.utils.enums.BorrowInfoStatusEnum;
import com.xxl.common.utils.enums.BorrowerStatusEnum;
import com.xxl.common.utils.enums.UserBindEnum;
import com.xxl.common.utils.result.ResponseEnum;
import com.xxl.core.entity.BorrowInfo;
import com.xxl.core.entity.Borrower;
import com.xxl.core.entity.IntegralGrade;
import com.xxl.core.entity.UserInfo;
import com.xxl.core.mapper.BorrowInfoMapper;
import com.xxl.core.service.*;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xxl.core.vo.BorrowInfoApprovalVO;
import com.xxl.core.vo.BorrowerDetailVO;
import com.xxl.core.vo.BorrowerInfoVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 借款信息表 服务实现类
 * </p>
 *
 * @author xxl
 * @since 2022-10-01
 */
@Service
public class BorrowInfoServiceImpl extends ServiceImpl<BorrowInfoMapper, BorrowInfo> implements BorrowInfoService {
    @Autowired
    IntegralGradeService integralGradeService;
    @Autowired
    UserInfoService userInfoService;
    @Autowired
    BorrowerService borrowerService;
    @Autowired
    DictService dictService;
    @Autowired
    LendService lendService;
    @Override
    public BigDecimal getBorrowAmount(Long userId) {
        //获取用户积分
        UserInfo userInfo = userInfoService.getById(userId);
        if (StringUtils.isEmpty(userInfo)){
            throw new BusinessException(ResponseEnum.LOGIN_AUTH_ERROR);
        }
        Integer integral = userInfo.getIntegral();
        //根据积分查询借款额度
        IntegralGrade integralGrade = integralGradeService.getOne(new QueryWrapper<IntegralGrade>().le("integral_start", integral).ge("integral_end", integral));
        if(integralGrade == null){
            return new BigDecimal("0");
        }
        //返回借款额度
        return integralGrade.getBorrowAmount();

    }

    @Override
    public void saveBorrowInfo(BorrowInfo borrowInfo, Long userId) {
        //获取userInfo的用户数据
        UserInfo userInfo = userInfoService.getById(userId);
        //判断用户绑定状态
        Assert.isTrue(userInfo.getBindStatus().intValue() == UserBindEnum.BIND_OK.getStatus().intValue(),
                ResponseEnum.USER_NO_BIND_ERROR);
        //判断用户信息是否审批通过
        Assert.isTrue(userInfo.getBorrowAuthStatus().intValue() == BorrowerStatusEnum.AUTH_OK.getStatus().intValue(),
                ResponseEnum.USER_NO_AMOUNT_ERROR);
        //判断借款额度是否足够
        BigDecimal borrowAmount = this.getBorrowAmount(userId);
        Assert.isTrue(borrowInfo.getAmount().doubleValue() <= borrowAmount.doubleValue(),
                ResponseEnum.USER_AMOUNT_LESS_ERROR);

        //存储数据
        borrowInfo.setUserId(userId);
        //百分比转成小数
        borrowInfo.setBorrowYearRate( borrowInfo.getBorrowYearRate().divide(new BigDecimal(100)));
            borrowInfo.setStatus(BorrowInfoStatusEnum.CHECK_RUN.getStatus());
        baseMapper.insert(borrowInfo);
    }

    @Override
    public Integer getStatusByUserId(Long userId) {
        QueryWrapper<BorrowInfo> borrowInfoQueryWrapper = new QueryWrapper<>();
        borrowInfoQueryWrapper.select("status").eq("user_id", userId);
        List<Object> objects = baseMapper.selectObjs(borrowInfoQueryWrapper);

        if(objects.size() == 0){
            //借款人尚未提交信息
            return BorrowInfoStatusEnum.NO_AUTH.getStatus();
        }
        Integer status = (Integer)objects.get(0);
        return status;
    }

    @Override
    public List<BorrowerInfoVo> getBorrowerList() {
        List<BorrowInfo> borrowInfos = baseMapper.selectList(null);
        List<BorrowerInfoVo> collect = borrowInfos.stream().map(borrowInfo -> {
            BorrowerInfoVo borrowerInfoVo = new BorrowerInfoVo();
            BeanUtils.copyProperties(borrowInfo, borrowerInfoVo);
            Borrower borrower = borrowerService.getByUserId(borrowInfo.getUserId());
            //封装扩展字段
            borrowerInfoVo.setName(borrower.getName());
            borrowerInfoVo.setMobile(borrower.getMobile());
            String returnMethod = dictService.getNameByParentDictCodeAndValue("returnMethod", borrowInfo.getReturnMethod());
            String moneyUse = dictService.getNameByParentDictCodeAndValue("moneyUse", borrowInfo.getMoneyUse());
            String status = BorrowInfoStatusEnum.getMsgByStatus(borrowInfo.getStatus());
            borrowerInfoVo.getParam().put("returnMethod", returnMethod);
            borrowerInfoVo.getParam().put("moneyUse", moneyUse);
            borrowerInfoVo.getParam().put("status", status);
            return borrowerInfoVo;
        }).collect(Collectors.toList());
        return collect;
    }

    @Override
    public void approval(BorrowInfoApprovalVO borrowInfoApprovalVO) {
        //修改借款信息状态
        BorrowInfo borrowInfo = baseMapper.selectById(borrowInfoApprovalVO.getId());
        borrowInfo.setStatus(borrowInfoApprovalVO.getStatus());
        baseMapper.updateById(borrowInfo);
        //审核通过则创建标的
        if (borrowInfoApprovalVO.getStatus().intValue() == BorrowInfoStatusEnum.CHECK_OK.getStatus().intValue()) {
            //TODO 创建标的
            lendService.createLend(borrowInfoApprovalVO,borrowInfo);

        }

    }

    @Override
    public Map<String, Object> getBorrowerDetail(Long id) {
        BorrowInfo borrowInfo = baseMapper.selectById(id);
        BorrowerInfoVo borrowerInfoVo = new BorrowerInfoVo();
        BeanUtils.copyProperties(borrowInfo,borrowerInfoVo);
        //组装数据
        String returnMethod = dictService.getNameByParentDictCodeAndValue("returnMethod", borrowInfo.getReturnMethod());
        String moneyUse = dictService.getNameByParentDictCodeAndValue("moneyUse", borrowInfo.getMoneyUse());
        String status = BorrowInfoStatusEnum.getMsgByStatus(borrowInfo.getStatus());
        borrowerInfoVo.setReturnMethod(returnMethod);
        borrowerInfoVo.setMoneyUse(moneyUse);
        borrowerInfoVo.setStatus(status);

        Borrower borrower = borrowerService.getByUserId(borrowerInfoVo.getUserId());
        BorrowerDetailVO borrowerDetailVO = borrowerService.getBorrowerInfo(borrower.getId());

        Map<String, Object> map = new HashMap<>();
        map.put("borrowInfo",borrowerInfoVo);
        map.put("borrower",borrowerDetailVO);
        return map;


    }

    @Override
    public BorrowInfo getByUserId(Long userId) {
       return baseMapper.selectOne(new QueryWrapper<BorrowInfo>().eq("user_id", userId));
    }


}
