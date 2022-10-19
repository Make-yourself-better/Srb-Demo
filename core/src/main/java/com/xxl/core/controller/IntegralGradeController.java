package com.xxl.core.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xxl.common.utils.result.R;
import com.xxl.core.entity.IntegralGrade;
import com.xxl.core.service.IntegralGradeService;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 积分等级表 前端控制器
 * </p>
 *
 * @author xxl
 * @since 2022-10-01
 */
@RestController
@RequestMapping("/admin/core/integral-grade")
public class IntegralGradeController {
    @Autowired
    IntegralGradeService integralGradeService;

    @GetMapping("list")
    public R GetIntegralGrade(){
        List<IntegralGrade> list = integralGradeService.list();
        return R.ok().data("getIntegralGradeList",list);
    }
    @PostMapping("DelIntegralGrade/{id}")
    public R DelIntegralGrade(@PathVariable("id") Long id){
        integralGradeService.removeById(id);
        return R.ok();
    }
    @PutMapping("/upIntegralGrade")
    public R upIntegralGrade(@RequestBody IntegralGrade integralGrade){
        integralGradeService.updateById(integralGrade);
        return R.ok();
    }
    @PostMapping("SaveIntegralGrade")
    public R SaveIntegralGrade(@RequestBody IntegralGrade integralGrade){
        integralGradeService.save(integralGrade);
        return R.ok();
    }
}

