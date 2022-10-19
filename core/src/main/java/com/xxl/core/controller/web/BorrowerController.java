package com.xxl.core.controller.web;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xxl.common.utils.Exception.BusinessException;
import com.xxl.common.utils.Jwt.JwtUtils;
import com.xxl.common.utils.result.R;

import com.xxl.common.utils.result.ResponseEnum;
import com.xxl.core.entity.Borrower;
import com.xxl.core.service.BorrowerService;
import com.xxl.core.vo.BorrowerApprovalVO;
import com.xxl.core.vo.BorrowerDetailVO;
import com.xxl.core.vo.BorrowerVO;

import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.security.PublicKey;
import java.util.List;

/**
 * <p>
 * 借款人 前端控制器
 * </p>
 *
 * @author xxl
 * @since 2022-10-01
 */
@RestController
@RequestMapping("/admin/core/borrower")
public class BorrowerController {
    @Autowired
    BorrowerService borrowerService;
    @PostMapping("saveBorrower")
    public R saveBorrower(@RequestBody BorrowerVO borrowerVO,HttpServletRequest request){
        Long userId = JwtUtils.getUserId(request.getHeader("token"));
        if (StringUtils.isEmpty(userId)){
            throw new BusinessException(ResponseEnum.LOGIN_AUTH_ERROR);
        }
        borrowerService.saveBorrower(borrowerVO,userId);
        return R.ok();
    }
    @ApiOperation("获取借款人认证状态")
    @GetMapping("/auth/getBorrowerStatus")
    public R getBorrowerStatus(HttpServletRequest request){
        Long userId = JwtUtils.getUserId(request.getHeader("token"));
        Integer status = borrowerService.getStatusByUserId(userId);
        return R.ok().data("borrowerStatus", status);
    }

    @GetMapping("/list/{page}/{limit}")
    public R audit(@PathVariable("page")Long page,
                   @PathVariable("limit") Long limit,
                   @RequestParam(value = "keyword",required = false) String keyword){
        Page<Borrower> borrowerPage = new Page<>(page, limit);
       IPage<Borrower> pageModel= borrowerService.auditList(borrowerPage,keyword);
        return R.ok().data("pageModel", pageModel);
    }
    @ApiOperation("获取借款人信息")
    @GetMapping("auditInfo/{id}")
    public R auditInfo(@PathVariable("id")Long id){
        BorrowerDetailVO borrowerDetailVO = borrowerService.getBorrowerInfo(id);
        return R.ok().data("borrowerVO",borrowerDetailVO);

    }
    @PostMapping("approval")
    public R approval(@RequestBody BorrowerApprovalVO borrowerApprovalVO){
        borrowerService.approval(borrowerApprovalVO);
        return R.ok().message("审批完成");
    }

}

