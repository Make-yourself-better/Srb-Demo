package com.xxl.thirdservice.controller;


import com.xxl.common.utils.result.R;
import com.xxl.thirdservice.service.OssuploadService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;


@RestController
@RequestMapping("api/ali/fileoss")
public class FileController {
    @Resource
    OssuploadService ossuploadService;
    @PostMapping("/upload")
    public R upload(@RequestPart("file") MultipartFile file ) {
        //获取上传文件 MultipartFile
        //返回上传路径
        String url= ossuploadService.uploadFile(file);
        return R.ok().message("文件上传成功").data("url",url);

    }
    @ApiOperation("文件删除")
    @DeleteMapping("/removeFile")
    public  R removeFile( @ApiParam(value = "要删除的文件路径", required = true)
                          @RequestParam("url") String url){
        ossuploadService.removeFile(url);
        return R.ok().message("删除成功");
    }
}
