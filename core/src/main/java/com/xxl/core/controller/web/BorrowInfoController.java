package com.xxl.core.controller.web;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xxl.common.utils.Exception.BusinessException;
import com.xxl.common.utils.Jwt.JwtUtils;
import com.xxl.common.utils.result.R;
import com.xxl.common.utils.result.ResponseEnum;
import com.xxl.core.entity.BorrowInfo;
import com.xxl.core.hfb.RequestHelper;
import com.xxl.core.service.BorrowInfoService;
import com.xxl.core.vo.BorrowInfoApprovalVO;
import com.xxl.core.vo.BorrowerInfoVo;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 借款信息表 前端控制器
 * </p>
 *
 * @author xxl
 * @since 2022-10-01
 */
@RestController
@RequestMapping("/admin/core/borrowinfo")
public class BorrowInfoController {
    @Autowired
    BorrowInfoService borrowInfoService;

    @ApiOperation("获取借款额度")
    @GetMapping("/auth/getBorrowAmount")
    public R getBorrowAmount(HttpServletRequest request){
        Long userId = JwtUtils.getUserId(request.getHeader("token"));
        BigDecimal borrowAmount = borrowInfoService.getBorrowAmount(userId);
        return R.ok().data("borrowAmount",borrowAmount);
    }
    @ApiOperation("提交借款申请")
    @PostMapping("/auth/save")
    public R save(@RequestBody BorrowInfo borrowInfo, HttpServletRequest request) {

        Long userId = JwtUtils.getUserId(request.getHeader("token"));
        borrowInfoService.saveBorrowInfo(borrowInfo, userId);
        return R.ok().message("提交成功");
    }
    @ApiOperation("获取借款申请审批状态")
    @GetMapping("/auth/getBorrowInfoStatus")
    public R getBorrowerStatus(HttpServletRequest request){
        Long userId = JwtUtils.getUserId(request.getHeader("token"));
        Integer status = borrowInfoService.getStatusByUserId(userId);
        return R.ok().data("borrowInfoStatus", status);
    }

    @ApiOperation("获取借款人列表")
    @GetMapping("/auth/getBorrowerList")
    public R getBorrowerList(){
      List<BorrowerInfoVo> borrowInfoList = borrowInfoService.getBorrowerList();
        return  R.ok().data("borrowInfoList",borrowInfoList);
    }
    @ApiOperation("获取借款人详情")
    @GetMapping("/show/{id}")
    public R BorrowerDetail(@PathVariable("id") Long id){
        Map<String, Object> borrowInfoDetail =  borrowInfoService.getBorrowerDetail(id);
        return R.ok().data("borrowInfoDetail",borrowInfoDetail);
    }
    @ApiOperation("审批")
    @PostMapping("/approval")
    public R approval(@RequestBody BorrowInfoApprovalVO borrowInfoApprovalVO ){
         borrowInfoService.approval(borrowInfoApprovalVO);
        return  R.ok();
    }



}

