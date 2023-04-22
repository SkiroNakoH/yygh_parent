package com.atguigu.yygh.oos.controller;

import com.atguigu.yygh.common.utils.QiniuUtils;
import com.atguigu.yygh.common.utils.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.joda.time.DateTime;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Api(tags = "文件相关接口")
@RestController
@RequestMapping("/admin/oss/file")
public class FileController {
    @ApiOperation("文件上传")
    @PostMapping("/upload")
    public Result upload(@ApiParam(name = "file", value = "文件", required = true)
                         @RequestParam("file") MultipartFile multipartFile) throws IOException {

        byte[] fileBytes = multipartFile.getBytes();
        String dateTime = new DateTime().toString("yyyy/MM/dd/");

        String fileName = dateTime + UUID.randomUUID().toString().replace("-", "") + "." +
                multipartFile.getOriginalFilename().split("\\.")[1];

        QiniuUtils.upload2Qiniu(fileBytes, fileName);

        String urlName = QiniuUtils.getUrlName(fileName);

        return Result.ok().data("url", urlName);
    }
}
