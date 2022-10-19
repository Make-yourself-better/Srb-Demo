package com.xxl.core.controller;


import com.alibaba.excel.EasyExcel;
import com.xxl.common.utils.Exception.BusinessException;
import com.xxl.common.utils.result.R;
import com.xxl.core.entity.Dict;
import com.xxl.core.service.DictService;
import com.xxl.core.to.ExcelDictDTO;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.List;

import static com.xxl.common.utils.result.ResponseEnum.UPLOAD_ERROR;

/**
 * <p>
 * 数据字典 前端控制器
 * </p>
 *
 * @author xxl
 * @since 2022-10-01
 */
@RestController
@RequestMapping("/admin/core/dict")
public class DictController {
    @Autowired
    DictService dictService;

    @GetMapping("/findByDictCode/{dictCode}")
    public R findByDictCode(@PathVariable("dictCode") String dictCode){
        List<Dict> dictList= dictService.findByDictCode(dictCode);
        return R.ok().data("dictList",dictList);
    }

    @GetMapping("GetDictList")
    public R GetDictList(){
        List<Dict> dictList  =  dictService.listWithTree();
        return R.ok().data("list",dictList);
    }

    @PutMapping("upDictList")
    public R upDictList(@RequestBody Dict dict){
        dictService.updateById(dict);
        return R.ok();
    }
    @PostMapping("saveDictList")
    public R saveDictList(@RequestBody Dict dict){
        dictService.save(dict);
        return R.ok();
    }
    @PostMapping("delDict")
    public R delDict(@RequestBody Long[] ids){
        dictService.removeByIds(Arrays.asList(ids));
        return R.ok();
    }


    @ApiOperation("Excel批量导入数据字典")
    @PostMapping("/import")
    public R batchImport(@RequestParam("file") MultipartFile file) {
        try {
            InputStream inputStream = file.getInputStream();
            dictService.batchImport(inputStream);
            return R.ok().message("批量导入成功");
        } catch (IOException e) {
            throw new BusinessException( UPLOAD_ERROR);
        }
    }

    @ApiOperation("Excel数据的导出")
    @GetMapping("/export")
    public void export(HttpServletResponse response){
        try {
            // 这里注意 有同学反应使用swagger 会导致各种问题，请直接用浏览器或者用postman
            response.setContentType("application/vnd.ms-excel");
            response.setCharacterEncoding("utf-8");
            // 这里URLEncoder.encode可以防止中文乱码 当然和easyexcel没有关系
            String fileName = URLEncoder.encode("mydict", "UTF-8").replaceAll("\\+", "%20");
            response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");
            EasyExcel.write(response.getOutputStream(), ExcelDictDTO.class).sheet("数据字典").doWrite(dictService.listDictData());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

