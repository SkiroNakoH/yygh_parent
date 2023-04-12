package com.atguigu.yygh.cmn.controller;


import com.atguigu.yygh.cmn.service.DictService;
import com.atguigu.yygh.common.utils.Result;
import com.atguigu.yygh.model.cmn.Dict;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * <p>
 * 组织架构表 前端控制器
 * </p>
 *
 * @author atguigu
 * @since 2023-04-11
 */
@Api(tags = "数据字典")
@CrossOrigin //解决跨域访问
@RestController
@RequestMapping("/admin/cmn/dict")
public class DictController {

    @Autowired
    private DictService dictService;

    @ApiOperation("字典列表查询")
    @GetMapping({"/findListByParentId/{parentId}","/findListByParentId"})
    public Result findListByParentId(@ApiParam("父Id") @PathVariable(required = false) Long parentId) {
        if (parentId == null) {
            parentId = 1L;
        }

        List<Dict> dictList = dictService.findListByParentId(parentId);
        return Result.ok().data("dictList",dictList);
    }

    /**
     * 读取数据库，上传Excel,让前端下载Excel
     * @param response 响应，数据载体
     * @throws IOException 文件上传IO异常
     */
    @ApiOperation("下载数据字典Excel表")
    @GetMapping("/downLoad")
    public void downLoad(HttpServletResponse response) throws IOException {
        dictService.downLoad(response);

    }

    /**
     * 将前端上传的Excel表下载解析，更新到数据库中
     * @param srcFile 上传的Excel文件
     * @return 上传成功，响应20000状态码
     */
    @ApiOperation("上传数据字典Excel表")
    @PostMapping("/upLoad")
    public Result upLoad(MultipartFile srcFile) throws IOException {
        dictService.upLoad(srcFile);

        return Result.ok();
    }
}

